package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdminAuditLogRecord
import com.example.data.model.CategoryItem
import com.example.data.model.GeoCatalog
import com.example.data.model.PortfolioPhoto
import com.example.data.model.ProfessionalProfile
import com.example.data.model.ReportRecord
import com.example.data.model.ReviewItem
import com.example.data.model.ServiceItem
import com.example.data.model.UserAccount
import com.example.data.repository.FirestoreProxiRepository
import com.example.ui.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class SearchFilter(
    val query: String = "",
    val countryCode: String = "BJ",
    val department: String = "",
    val city: String = "Cotonou",
    val neighborhood: String = "",
    val categoryId: String = ""
)

data class WizardDraft(
    val id: String = "pro_" + UUID.randomUUID().toString().replace("-", "").take(12),
    val businessName: String = "",
    val primaryCategoryId: String = "cat_plumbing",
    val description: String = "",
    val ownerName: String = "",
    val profileImage: String = "",
    val countryCode: String = "BJ",
    val countryName: String = "Bénin",
    val department: String = "Littoral",
    val city: String = "Cotonou",
    val neighborhood: String = "",
    val interventionZones: String = "",
    val services: List<ServiceItem> = emptyList(),
    val phonePublic: String = "",
    val whatsappNumber: String = "",
    val openingHours: String = "Lun - Sam : 08h00 - 18h30",
    val consentGiven: Boolean = false,
    val portfolioPhotos: List<Pair<String, String>> = emptyList(),
    val currentStep: Int = 1
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProxiAfrikViewModel(
    private val repository: FirestoreProxiRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _uiMessages = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val uiMessages: SharedFlow<String> = _uiMessages.asSharedFlow()

    // --- Navigation Stack ---
    private val _navigationStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = _navigationStack
        .map { stack -> stack.lastOrNull() ?: Screen.Home }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Home)

    fun navigateTo(screen: Screen) {
        if (screen is Screen.AdminDashboard && !isAdmin.value) {
            _uiMessages.tryEmit("Accès réservé aux administrateurs.")
            return
        }
        _navigationStack.value = _navigationStack.value + screen
    }

    fun navigateBack(): Boolean {
        if (_navigationStack.value.size > 1) {
            _navigationStack.value = _navigationStack.value.dropLast(1)
            return true
        }
        return false
    }

    fun navigateHome() {
        _navigationStack.value = listOf(Screen.Home)
    }

    // --- User & Role (from Firestore DB) ---
    init {
        viewModelScope.launch {
            val firebaseUser = Firebase.auth.currentUser
            if (firebaseUser != null && firebaseUser.uid == currentUserId) {
                repository.ensureUserAccount(firebaseUser)
            }
        }
    }

    val currentUser: StateFlow<UserAccount?> = repository.observeUserAccount(currentUserId)
        .safeFallback(null, "observeUserAccount")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val isAdmin: StateFlow<Boolean> = combine(
        repository.observeIsAdmin(currentUserId).safeFallback(false, "observeIsAdmin"),
        currentUser
    ) { adminDocOrEmail, account ->
        adminDocOrEmail || account?.role == "admin"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), repository.isBootstrappedAdmin())

    fun updateUserRole(newRole: String) {
        viewModelScope.launch {
            val result = repository.updateOwnRole(newRole)
            if (result.isFailure) {
                _uiMessages.tryEmit("Impossible de modifier le rôle : action non autorisée.")
            }
        }
    }

    // --- Categories & Geography ---
    val allCategories: StateFlow<List<CategoryItem>> = repository.observeCategories()
        .safeFallback(GeoCatalog.defaultCategories, "observeCategories")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), GeoCatalog.defaultCategories)

    val activeCategories: StateFlow<List<CategoryItem>> = allCategories
        .map { list -> list.filter { it.isActive } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), GeoCatalog.defaultCategories)

    val countries = GeoCatalog.supportedCountries

    private val _selectedCountry = MutableStateFlow(GeoCatalog.supportedCountries.first())
    val selectedCountry: StateFlow<GeoCatalog.CountryInfo> = _selectedCountry.asStateFlow()

    private val _selectedDepartment = MutableStateFlow("Littoral")
    val selectedDepartment: StateFlow<String> = _selectedDepartment.asStateFlow()

    private val _selectedCity = MutableStateFlow("Cotonou")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    fun setCountry(country: GeoCatalog.CountryInfo) {
        _selectedCountry.value = country
        val firstDept = country.departments.firstOrNull()
        _selectedDepartment.value = firstDept?.name ?: ""
        _selectedCity.value = firstDept?.cities?.firstOrNull() ?: country.allCities.firstOrNull() ?: ""
        _searchFilter.value = _searchFilter.value.copy(
            countryCode = country.code,
            department = _selectedDepartment.value,
            city = _selectedCity.value
        )
    }

    fun setDepartment(deptName: String) {
        _selectedDepartment.value = deptName
        val dept = selectedCountry.value.departments.find { it.name.equals(deptName, ignoreCase = true) }
        val defaultCity = dept?.cities?.firstOrNull() ?: ""
        _selectedCity.value = defaultCity
        _searchFilter.value = _searchFilter.value.copy(
            department = deptName,
            city = defaultCity
        )
    }

    fun setCity(city: String) {
        _selectedCity.value = city
        val foundDept = selectedCountry.value.departments.find { it.cities.contains(city) }
        if (foundDept != null) {
            _selectedDepartment.value = foundDept.name
        }
        _searchFilter.value = _searchFilter.value.copy(
            department = foundDept?.name ?: _selectedDepartment.value,
            city = city
        )
    }

    // --- Real Search Engine (exclusively APPROVED + PUBLISHED profiles) ---
    private val _searchFilter = MutableStateFlow(SearchFilter())
    val searchFilter: StateFlow<SearchFilter> = _searchFilter.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchFilter.value = _searchFilter.value.copy(query = query)
    }

    fun updateSearchCategory(categoryId: String) {
        _searchFilter.value = _searchFilter.value.copy(categoryId = categoryId)
    }

    fun updateSearchLocation(
        countryCode: String,
        department: String = "",
        city: String = "",
        neighborhood: String = ""
    ) {
        _searchFilter.value = _searchFilter.value.copy(
            countryCode = countryCode,
            department = department,
            city = city,
            neighborhood = neighborhood
        )
    }

    val searchResults: StateFlow<List<ProfessionalProfile>> = _searchFilter
        .flatMapLatest { filter ->
            repository.searchApprovedProfiles(
                queryText = filter.query,
                countryCode = filter.countryCode,
                department = filter.department,
                city = filter.city,
                neighborhood = filter.neighborhood,
                categoryId = filter.categoryId
            ).safeFallback(emptyList(), "searchApprovedProfiles")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val approvedProfiles: StateFlow<List<ProfessionalProfile>> = repository.observeApprovedProfiles()
        .safeFallback(emptyList(), "observeApprovedProfiles")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    // --- Profile Detail ---
    private val _selectedProfileId = MutableStateFlow<String?>(null)

    val selectedProfile: StateFlow<ProfessionalProfile?> = _selectedProfileId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.observeProfileById(id).safeFallback(null, "observeProfileById")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val selectedProfileServices: StateFlow<List<ServiceItem>> = _selectedProfileId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.observeServicesForProfile(id).safeFallback(emptyList(), "observeServices")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val selectedProfilePortfolio: StateFlow<List<PortfolioPhoto>> = _selectedProfileId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.observePortfolioForProfile(id).safeFallback(emptyList(), "observePortfolio")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val selectedProfileReviews: StateFlow<List<ReviewItem>> = _selectedProfileId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.observeApprovedReviews(id).safeFallback(emptyList(), "observeApprovedReviews")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    fun selectProfile(profileId: String) {
        _selectedProfileId.value = profileId
        viewModelScope.launch {
            val pro = approvedProfiles.value.find { it.id == profileId }
            if (pro != null && pro.verificationStatus == "APPROVED") {
                repository.incrementProfileCounter(profileId, "viewsCount")
            }
        }
        navigateTo(Screen.ProfileDetail(profileId))
    }

    fun contactViaWhatsApp(context: Context, pro: ProfessionalProfile) {
        if (pro.verificationStatus == "APPROVED") {
            viewModelScope.launch {
                repository.incrementProfileCounter(pro.id, "whatsappClicksCount")
            }
        }
        try {
            val rawNumber = pro.whatsappNumber.replace("[^0-9]".toRegex(), "")
            if (rawNumber.length < 6) {
                Toast.makeText(context, "Numéro WhatsApp invalide.", Toast.LENGTH_SHORT).show()
                return
            }
            val defaultMsg = "Bonjour ${pro.businessName}, j'ai découvert votre fiche sur ProxiAfrik et je souhaite obtenir des renseignements sur vos services."
            val url = "https://api.whatsapp.com/send?phone=$rawNumber&text=${Uri.encode(defaultMsg)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Impossible d'ouvrir WhatsApp : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun contactViaPhone(context: Context, pro: ProfessionalProfile) {
        if (pro.verificationStatus == "APPROVED") {
            viewModelScope.launch {
                repository.incrementProfileCounter(pro.id, "phoneClicksCount")
            }
        }
        try {
            val cleanTel = pro.phonePublic.trim()
            if (cleanTel.isBlank()) {
                Toast.makeText(context, "Numéro de téléphone manquant.", Toast.LENGTH_SHORT).show()
                return
            }
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanTel")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Impossible de composer le numéro : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun submitReport(profileId: String, reason: String, description: String) {
        viewModelScope.launch {
            val reporterName = currentUser.value?.fullName?.ifBlank { "Utilisateur" } ?: "Utilisateur"
            val result = repository.submitReport(profileId, reason, description, reporterName)
            if (result.isSuccess) {
                _uiMessages.tryEmit("Signalement enregistré et transmis à l'administration.")
            } else {
                _uiMessages.tryEmit("Erreur lors de l'envoi du signalement.")
            }
        }
    }

    fun submitReview(profileId: String, rating: Int, comment: String, customAuthorName: String = "") {
        viewModelScope.launch {
            val author = customAuthorName.ifBlank {
                currentUser.value?.fullName?.ifBlank { "Client ProxiAfrik" } ?: "Client ProxiAfrik"
            }
            val result = repository.submitReview(profileId, rating, comment, author)
            if (result.isSuccess) {
                _uiMessages.tryEmit("Votre avis a été publié. Merci !")
            } else {
                _uiMessages.tryEmit("Impossible de publier l'avis.")
            }
        }
    }

    // --- Professional Wizard ---
    private val _wizardDraft = MutableStateFlow(WizardDraft())
    val wizardDraft: StateFlow<WizardDraft> = _wizardDraft.asStateFlow()

    private val _isSubmittingWizard = MutableStateFlow(false)
    val isSubmittingWizard: StateFlow<Boolean> = _isSubmittingWizard.asStateFlow()

    fun updateWizardField(update: (WizardDraft) -> WizardDraft) {
        _wizardDraft.value = update(_wizardDraft.value)
    }

    fun nextWizardStep() {
        if (_wizardDraft.value.currentStep < 6) {
            _wizardDraft.value = _wizardDraft.value.copy(currentStep = _wizardDraft.value.currentStep + 1)
        }
    }

    fun prevWizardStep() {
        if (_wizardDraft.value.currentStep > 1) {
            _wizardDraft.value = _wizardDraft.value.copy(currentStep = _wizardDraft.value.currentStep - 1)
        }
    }

    fun submitWizard(onSuccess: () -> Unit = {}) {
        if (_isSubmittingWizard.value) return
        viewModelScope.launch {
            _isSubmittingWizard.value = true
            try {
                val draft = _wizardDraft.value
                val profile = ProfessionalProfile(
                    id = draft.id,
                    userId = currentUserId,
                    ownerName = draft.ownerName.ifBlank { currentUser.value?.fullName ?: "" },
                    businessName = draft.businessName.trim(),
                    description = draft.description.trim(),
                    countryCode = draft.countryCode,
                    countryName = draft.countryName,
                    department = draft.department,
                    city = draft.city,
                    neighborhood = draft.neighborhood.trim(),
                    interventionZones = draft.interventionZones.trim(),
                    phonePublic = draft.phonePublic.trim(),
                    whatsappNumber = draft.whatsappNumber.trim(),
                    profileImage = draft.profileImage,
                    primaryCategoryId = draft.primaryCategoryId,
                    verificationStatus = "PENDING",
                    publicationStatus = "PUBLISHED",
                    openingHours = draft.openingHours.trim(),
                    consentGiven = draft.consentGiven
                )
                val photos = draft.portfolioPhotos.map { (dataUrl, caption) ->
                    PortfolioPhoto(imageUrl = dataUrl, caption = caption)
                }
                val result = repository.submitProfile(profile, draft.services, photos)
                if (result.isSuccess) {
                    _wizardDraft.value = WizardDraft()
                    _uiMessages.tryEmit("Profil enregistré en statut « En attente » de validation administrative.")
                    onSuccess()
                    navigateTo(Screen.ProDashboard)
                } else {
                    _uiMessages.tryEmit("Erreur : vérifiez que tous les champs obligatoires sont valides.")
                }
            } finally {
                _isSubmittingWizard.value = false
            }
        }
    }

    // --- Pro Dashboard State ---
    val currentProProfile: StateFlow<ProfessionalProfile?> = repository.observeMyProfile()
        .safeFallback(null, "observeMyProfile")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    val currentProServices: StateFlow<List<ServiceItem>> = currentProProfile
        .flatMapLatest { pro ->
            if (pro == null) flowOf(emptyList())
            else repository.observeServicesForProfile(pro.id).safeFallback(emptyList(), "proServices")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val currentProPortfolio: StateFlow<List<PortfolioPhoto>> = currentProProfile
        .flatMapLatest { pro ->
            if (pro == null) flowOf(emptyList())
            else repository.observePortfolioForProfile(pro.id).safeFallback(emptyList(), "proPortfolio")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    fun addServiceForCurrentPro(title: String, description: String, price: Double, pricingType: String) {
        val pro = currentProProfile.value ?: return
        viewModelScope.launch {
            val res = repository.addService(pro.id, pro.primaryCategoryId, title, description, price, pricingType)
            if (res.isFailure) _uiMessages.tryEmit("Impossible d'ajouter ce service.")
        }
    }

    fun deleteService(serviceId: String) {
        val pro = currentProProfile.value ?: return
        viewModelScope.launch {
            val res = repository.deleteService(pro.id, serviceId)
            if (res.isFailure) _uiMessages.tryEmit("Impossible de supprimer ce service.")
        }
    }

    fun addPortfolioImageForCurrentPro(imageDataUrl: String, caption: String) {
        val pro = currentProProfile.value ?: return
        viewModelScope.launch {
            val res = repository.addPortfolioPhoto(pro.id, imageDataUrl, caption)
            if (res.isSuccess) {
                _uiMessages.tryEmit("Photo ajoutée à votre galerie.")
            } else {
                _uiMessages.tryEmit("Impossible d'ajouter la photo.")
            }
        }
    }

    fun deletePortfolioImage(imageId: String) {
        val pro = currentProProfile.value ?: return
        viewModelScope.launch {
            val res = repository.deletePortfolioPhoto(pro.id, imageId)
            if (res.isFailure) _uiMessages.tryEmit("Impossible de supprimer la photo.")
        }
    }

    // --- Admin Dashboard State & Actions (gated by isAdmin) ---
    val pendingProfiles: StateFlow<List<ProfessionalProfile>> = isAdmin
        .flatMapLatest { admin ->
            if (!admin) flowOf(emptyList())
            else repository.observePendingProfilesForAdmin().safeFallback(emptyList(), "pendingProfiles")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val allProfilesForAdmin: StateFlow<List<ProfessionalProfile>> = isAdmin
        .flatMapLatest { admin ->
            if (!admin) flowOf(emptyList())
            else repository.observeAllProfilesForAdmin().safeFallback(emptyList(), "allProfiles")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val allReports: StateFlow<List<ReportRecord>> = isAdmin
        .flatMapLatest { admin ->
            if (!admin) flowOf(emptyList())
            else repository.observeAllReportsForAdmin().safeFallback(emptyList(), "allReports")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val auditLogs: StateFlow<List<AdminAuditLogRecord>> = isAdmin
        .flatMapLatest { admin ->
            if (!admin) flowOf(emptyList())
            else repository.observeAuditLogsForAdmin().safeFallback(emptyList(), "auditLogs")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    fun approveProfile(profileId: String) {
        if (!isAdmin.value) return
        viewModelScope.launch {
            val res = repository.approveProfile(profileId)
            _uiMessages.tryEmit(if (res.isSuccess) "Profil validé et publié." else "Erreur : action refusée.")
        }
    }

    fun rejectProfile(profileId: String, reason: String) {
        if (!isAdmin.value) return
        viewModelScope.launch {
            val res = repository.rejectProfile(profileId, reason)
            _uiMessages.tryEmit(if (res.isSuccess) "Profil refusé." else "Erreur : action refusée.")
        }
    }

    fun suspendProfile(profileId: String, reason: String) {
        if (!isAdmin.value) return
        viewModelScope.launch {
            val res = repository.suspendProfile(profileId, reason)
            _uiMessages.tryEmit(if (res.isSuccess) "Profil suspendu." else "Erreur : action refusée.")
        }
    }

    fun toggleCategoryActive(category: CategoryItem) {
        if (!isAdmin.value) return
        viewModelScope.launch {
            val res = repository.toggleCategory(category, !category.isActive)
            if (res.isFailure) _uiMessages.tryEmit("Action refusée sur la catégorie.")
        }
    }

    fun addNewCategory(name: String, description: String, iconKey: String) {
        if (!isAdmin.value) return
        viewModelScope.launch {
            val nextOrder = (allCategories.value.maxOfOrNull { it.sortOrder } ?: 0) + 1
            val res = repository.addCategory(name, description, iconKey, nextOrder)
            _uiMessages.tryEmit(if (res.isSuccess) "Catégorie créée." else "Action refusée.")
        }
    }

    fun resolveReport(reportId: String, actionTaken: String) {
        if (!isAdmin.value) return
        viewModelScope.launch {
            val res = repository.updateReportStatus(reportId, actionTaken)
            _uiMessages.tryEmit(if (res.isSuccess) "Signalement mis à jour." else "Erreur : action refusée.")
        }
    }

    private fun <T> Flow<T>.safeFallback(default: T, label: String): Flow<T> = catch { error ->
        val fsError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (fsError == null && error is CancellationException) throw error
        Log.w(TAG, "Stream $label error: ${(fsError ?: error).localizedMessage}")
        emit(default)
    }

    private companion object {
        const val TAG = "ProxiAfrikVM"
    }
}

class ProxiAfrikViewModelFactory(
    private val repository: FirestoreProxiRepository,
    private val currentUserId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProxiAfrikViewModel::class.java)) {
            return ProxiAfrikViewModel(repository, currentUserId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

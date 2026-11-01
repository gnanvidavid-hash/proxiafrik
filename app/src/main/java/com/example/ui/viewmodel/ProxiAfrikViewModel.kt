package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AdminAuditLogEntity
import com.example.data.local.AppDatabase
import com.example.data.local.CategoryEntity
import com.example.data.local.PortfolioImageEntity
import com.example.data.local.ProfessionalProfileEntity
import com.example.data.local.ReportEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.SeedData
import com.example.data.local.ServiceEntity
import com.example.data.local.SubscriptionEntity
import com.example.data.local.UserEntity
import com.example.data.repository.ProxiAfrikRepository
import com.example.ui.navigation.LegalType
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
    val id: String = "pro_" + UUID.randomUUID().toString().take(8),
    val businessName: String = "",
    val primaryCategoryId: String = "cat_plumbing",
    val description: String = "",
    val ownerName: String = "",
    val countryCode: String = "BJ",
    val countryName: String = "Bénin",
    val department: String = "Littoral",
    val city: String = "Cotonou",
    val neighborhood: String = "",
    val interventionZones: String = "",
    val services: List<ServiceEntity> = emptyList(),
    val phonePublic: String = "",
    val whatsappNumber: String = "",
    val openingHours: String = "Lun - Sam : 08h00 - 18h30",
    val consentGiven: Boolean = false,
    val profileImage: String = "",
    val portfolioCaptions: List<Pair<String, String>> = emptyList(),
    val currentStep: Int = 1
)

class ProxiAfrikViewModel(
    application: Application,
    private val repository: ProxiAfrikRepository
) : AndroidViewModel(application) {

    // --- Navigation Stack ---
    private val _navigationStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = _navigationStack
        .combine(flowOf(Unit)) { stack, _ -> stack.lastOrNull() ?: Screen.Home }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Home)

    fun navigateTo(screen: Screen) {
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

    // --- Auth & User State ---
    val currentUser: StateFlow<UserEntity?> = repository.currentUser

    fun login(email: String, roleHint: String? = null) {
        viewModelScope.launch {
            repository.login(email, roleHint)
        }
    }

    fun register(fullName: String, email: String, phone: String, role: String) {
        viewModelScope.launch {
            repository.register(fullName, email, phone, role)
        }
    }

    fun logout() {
        repository.logout()
        navigateHome()
    }

    // Switch quick demo role (Client, Pro, Admin)
    fun switchDemoUser(role: String) {
        viewModelScope.launch {
            when (role) {
                "ADMIN" -> repository.login("admin@proxiafrik.com", "ADMIN")
                "PROFESSIONAL" -> repository.login("pro@proxiafrik.com", "PROFESSIONAL")
                else -> repository.login("client@proxiafrik.com", "CLIENT")
            }
        }
    }

    // --- Categories & Geography ---
    val activeCategories: StateFlow<List<CategoryEntity>> = repository.activeCategoriesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategoriesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val countries = SeedData.supportedCountries

    private val _selectedCountry = MutableStateFlow(SeedData.supportedCountries.first())
    val selectedCountry: StateFlow<SeedData.CountryInfo> = _selectedCountry.asStateFlow()

    private val _selectedDepartment = MutableStateFlow("Littoral")
    val selectedDepartment: StateFlow<String> = _selectedDepartment.asStateFlow()

    private val _selectedCity = MutableStateFlow("Cotonou")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    fun setCountry(country: SeedData.CountryInfo) {
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

    // --- Search Engine ---
    private val _searchFilter = MutableStateFlow(SearchFilter())
    val searchFilter: StateFlow<SearchFilter> = _searchFilter.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchFilter.value = _searchFilter.value.copy(query = query)
    }

    fun updateSearchCategory(categoryId: String) {
        _searchFilter.value = _searchFilter.value.copy(categoryId = categoryId)
    }

    fun updateSearchLocation(countryCode: String, department: String = "", city: String = "", neighborhood: String = "") {
        _searchFilter.value = _searchFilter.value.copy(
            countryCode = countryCode,
            department = department,
            city = city,
            neighborhood = neighborhood
        )
    }

    val searchResults: StateFlow<List<ProfessionalProfileEntity>> = _searchFilter
        .flatMapLatest { filter ->
            repository.searchApprovedProfiles(
                query = filter.query,
                countryCode = filter.countryCode,
                department = filter.department,
                city = filter.city,
                neighborhood = filter.neighborhood,
                categoryId = filter.categoryId
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val approvedProfiles: StateFlow<List<ProfessionalProfileEntity>> = repository.approvedProfilesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Profile Detail ---
    private val _selectedProfileId = MutableStateFlow<String?>(null)
    val selectedProfile: StateFlow<ProfessionalProfileEntity?> = _selectedProfileId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.getProfileByIdFlow(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedProfileServices: StateFlow<List<ServiceEntity>> = _selectedProfileId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getServicesForProfile(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedProfilePortfolio: StateFlow<List<PortfolioImageEntity>> = _selectedProfileId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getPortfolioForProfile(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedProfileReviews: StateFlow<List<ReviewEntity>> = _selectedProfileId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getApprovedReviews(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectProfile(profileId: String) {
        _selectedProfileId.value = profileId
        viewModelScope.launch {
            repository.recordProfileView(profileId)
        }
        navigateTo(Screen.ProfileDetail(profileId))
    }

    // Contact actions with actual measurement and Android Intents
    fun contactViaWhatsApp(context: Context, pro: ProfessionalProfileEntity) {
        viewModelScope.launch {
            repository.recordContactClick(pro.id, "WHATSAPP")
        }
        try {
            val rawNumber = pro.whatsappNumber.replace("[^0-9]".toRegex(), "")
            val defaultMsg = "Bonjour, j'ai découvert votre profil sur ProxiAfrik et je souhaite obtenir des informations sur vos services."
            val url = "https://api.whatsapp.com/send?phone=$rawNumber&text=${Uri.encode(defaultMsg)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Impossible d'ouvrir WhatsApp : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun contactViaPhone(context: Context, pro: ProfessionalProfileEntity) {
        viewModelScope.launch {
            repository.recordContactClick(pro.id, "PHONE")
        }
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${pro.phonePublic}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Impossible de composer le numéro : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun submitReport(profileId: String, reason: String, description: String) {
        viewModelScope.launch {
            val user = currentUser.value
            val report = ReportEntity(
                id = "rep_" + UUID.randomUUID().toString().take(8),
                reporterUserId = user?.id ?: "anonymous",
                reporterName = user?.fullName ?: "Visiteur",
                reportedProfileId = profileId,
                reason = reason,
                description = description
            )
            repository.submitReport(report)
        }
    }

    fun submitReview(profileId: String, rating: Int, comment: String, customAuthorName: String = "") {
        viewModelScope.launch {
            val user = currentUser.value
            val author = when {
                customAuthorName.isNotBlank() -> customAuthorName
                user != null && user.fullName.isNotBlank() -> user.fullName
                else -> "Client ProxiAfrik"
            }
            val review = ReviewEntity(
                id = "rev_" + UUID.randomUUID().toString().take(8),
                professionalId = profileId,
                authorUserId = user?.id ?: "client_anon",
                authorName = author,
                rating = rating,
                comment = comment,
                moderationStatus = "APPROVED"
            )
            repository.submitReview(review)
        }
    }

    // --- Professional Wizard (6-Step Registration) ---
    private val _wizardDraft = MutableStateFlow(WizardDraft())
    val wizardDraft: StateFlow<WizardDraft> = _wizardDraft.asStateFlow()

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

    fun submitWizard() {
        viewModelScope.launch {
            val draft = _wizardDraft.value
            val user = currentUser.value ?: return@launch
            val profile = ProfessionalProfileEntity(
                id = draft.id,
                userId = user.id,
                businessName = draft.businessName,
                slug = draft.businessName.lowercase().replace(" ", "-"),
                description = draft.description,
                countryCode = draft.countryCode,
                countryName = draft.countryName,
                department = draft.department,
                city = draft.city,
                neighborhood = draft.neighborhood,
                interventionZones = draft.interventionZones,
                phonePublic = draft.phonePublic,
                whatsappNumber = draft.whatsappNumber,
                primaryCategoryId = draft.primaryCategoryId,
                verificationStatus = "PENDING",
                publicationStatus = "PUBLISHED",
                openingHours = draft.openingHours,
                viewsCount = 0
            )
            repository.submitProfile(profile)

            // Save services
            draft.services.forEach { srv ->
                repository.addService(srv.copy(id = "srv_" + UUID.randomUUID().toString().take(8), professionalId = profile.id))
            }

            // Save portfolio captions
            draft.portfolioCaptions.forEach { (imgUrl, caption) ->
                repository.addPortfolioImage(
                    PortfolioImageEntity(
                        id = "img_" + UUID.randomUUID().toString().take(8),
                        professionalId = profile.id,
                        imageUrl = imgUrl,
                        caption = caption
                    )
                )
            }

            // Reset draft and navigate to Pro Dashboard
            _wizardDraft.value = WizardDraft()
            navigateTo(Screen.ProDashboard)
        }
    }

    // --- Pro Dashboard State ---
    val currentProProfile: StateFlow<ProfessionalProfileEntity?> = currentUser
        .flatMapLatest { user ->
            if (user == null || user.role != "PROFESSIONAL") flowOf(null)
            else repository.getProfileByUserIdFlow(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentProServices: StateFlow<List<ServiceEntity>> = currentProProfile
        .flatMapLatest { pro ->
            if (pro == null) flowOf(emptyList())
            else repository.getServicesForProfile(pro.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentProPortfolio: StateFlow<List<PortfolioImageEntity>> = currentProProfile
        .flatMapLatest { pro ->
            if (pro == null) flowOf(emptyList())
            else repository.getPortfolioForProfile(pro.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentProWhatsAppClicks: StateFlow<Int> = currentProProfile
        .flatMapLatest { pro ->
            if (pro == null) flowOf(0)
            else repository.getWhatsAppClicksCount(pro.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentProPhoneClicks: StateFlow<Int> = currentProProfile
        .flatMapLatest { pro ->
            if (pro == null) flowOf(0)
            else repository.getPhoneClicksCount(pro.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentProSubscription: StateFlow<SubscriptionEntity?> = currentProProfile
        .flatMapLatest { pro ->
            if (pro == null) flowOf(null)
            else repository.getSubscriptionForProfile(pro.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addServiceForCurrentPro(title: String, description: String, price: Double, pricingType: String) {
        viewModelScope.launch {
            val pro = currentProProfile.value ?: return@launch
            val srv = ServiceEntity(
                id = "srv_" + UUID.randomUUID().toString().take(8),
                professionalId = pro.id,
                categoryId = pro.primaryCategoryId,
                title = title,
                description = description,
                indicativePrice = price,
                pricingType = pricingType
            )
            repository.addService(srv)
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            repository.deleteService(serviceId)
        }
    }

    fun addPortfolioImageForCurrentPro(caption: String) {
        viewModelScope.launch {
            val pro = currentProProfile.value ?: return@launch
            val img = PortfolioImageEntity(
                id = "img_" + UUID.randomUUID().toString().take(8),
                professionalId = pro.id,
                imageUrl = "res://sample_work",
                caption = caption
            )
            repository.addPortfolioImage(img)
        }
    }

    fun deletePortfolioImage(imageId: String) {
        viewModelScope.launch {
            repository.deletePortfolioImage(imageId)
        }
    }

    // --- Admin Dashboard State & Actions ---
    val pendingProfiles: StateFlow<List<ProfessionalProfileEntity>> = repository.pendingProfilesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProfilesForAdmin: StateFlow<List<ProfessionalProfileEntity>> = repository.allProfilesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<ReportEntity>> = repository.allReportsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AdminAuditLogEntity>> = repository.auditLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun approveProfile(profileId: String) {
        viewModelScope.launch {
            val admin = currentUser.value?.id ?: "admin"
            repository.approveProfile(profileId, admin)
        }
    }

    fun rejectProfile(profileId: String, reason: String) {
        viewModelScope.launch {
            val admin = currentUser.value?.id ?: "admin"
            repository.rejectProfile(profileId, reason, admin)
        }
    }

    fun suspendProfile(profileId: String, reason: String) {
        viewModelScope.launch {
            val admin = currentUser.value?.id ?: "admin"
            repository.suspendProfile(profileId, reason, admin)
        }
    }

    fun toggleCategoryActive(categoryId: String, currentActive: Boolean) {
        viewModelScope.launch {
            val admin = currentUser.value?.id ?: "admin"
            repository.toggleCategory(categoryId, !currentActive, admin)
        }
    }

    fun addNewCategory(name: String, description: String, iconKey: String) {
        viewModelScope.launch {
            val admin = currentUser.value?.id ?: "admin"
            val newCat = CategoryEntity(
                id = "cat_" + UUID.randomUUID().toString().take(8),
                name = name.trim(),
                slug = name.trim().lowercase().replace(" ", "-"),
                description = description.trim(),
                iconKey = iconKey,
                isActive = true,
                sortOrder = (allCategories.value.maxOfOrNull { it.sortOrder } ?: 0) + 1
            )
            repository.addCategory(newCat, admin)
        }
    }

    fun resolveReport(reportId: String, actionTaken: String) {
        viewModelScope.launch {
            val admin = currentUser.value?.id ?: "admin"
            repository.updateReportStatus(reportId, actionTaken, admin)
        }
    }
}

class ProxiAfrikViewModelFactory(
    private val application: Application,
    private val repository: ProxiAfrikRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProxiAfrikViewModel::class.java)) {
            return ProxiAfrikViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

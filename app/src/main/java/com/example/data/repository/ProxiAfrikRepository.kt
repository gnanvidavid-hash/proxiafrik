package com.example.data.repository

import com.example.data.local.AdminAuditLogDao
import com.example.data.local.AdminAuditLogEntity
import com.example.data.local.CategoryDao
import com.example.data.local.CategoryEntity
import com.example.data.local.ContactEventDao
import com.example.data.local.ContactEventEntity
import com.example.data.local.PortfolioDao
import com.example.data.local.PortfolioImageEntity
import com.example.data.local.ProfessionalProfileDao
import com.example.data.local.ProfessionalProfileEntity
import com.example.data.local.ReportDao
import com.example.data.local.ReportEntity
import com.example.data.local.ReviewDao
import com.example.data.local.ReviewEntity
import com.example.data.local.SeedData
import com.example.data.local.ServiceDao
import com.example.data.local.ServiceEntity
import com.example.data.local.SubscriptionDao
import com.example.data.local.SubscriptionEntity
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import java.text.Normalizer
import java.util.UUID

class ProxiAfrikRepository(
    private val userDao: UserDao,
    private val profileDao: ProfessionalProfileDao,
    private val categoryDao: CategoryDao,
    private val serviceDao: ServiceDao,
    private val portfolioDao: PortfolioDao,
    private val reviewDao: ReviewDao,
    private val reportDao: ReportDao,
    private val subscriptionDao: SubscriptionDao,
    private val contactEventDao: ContactEventDao,
    private val auditDao: AdminAuditLogDao
) {
    // Current authenticated user state (persists across app interactions)
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun setCurrentUser(user: UserEntity?) {
        _currentUser.value = user
    }

    suspend fun login(email: String, roleHint: String? = null): Result<UserEntity> {
        val user = userDao.getUserByEmail(email.trim().lowercase())
        return if (user != null) {
            _currentUser.value = user
            Result.success(user)
        } else {
            // Auto register test account if needed
            val newUser = UserEntity(
                id = "user_" + UUID.randomUUID().toString().take(8),
                fullName = email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
                email = email.trim().lowercase(),
                phone = "+22997000000",
                role = roleHint ?: "CLIENT"
            )
            userDao.insertUser(newUser)
            _currentUser.value = newUser
            Result.success(newUser)
        }
    }

    suspend fun register(fullName: String, email: String, phone: String, role: String): Result<UserEntity> {
        val existing = userDao.getUserByEmail(email.trim().lowercase())
        if (existing != null) {
            return Result.failure(Exception("Un compte avec cet email existe déjà."))
        }
        val user = UserEntity(
            id = "user_" + UUID.randomUUID().toString().take(8),
            fullName = fullName.trim(),
            email = email.trim().lowercase(),
            phone = phone.trim(),
            role = role
        )
        userDao.insertUser(user)
        _currentUser.value = user
        return Result.success(user)
    }

    fun logout() {
        _currentUser.value = null
    }

    // --- Profiles ---
    val approvedProfilesFlow: Flow<List<ProfessionalProfileEntity>> = profileDao.getApprovedProfilesFlow()
    val pendingProfilesFlow: Flow<List<ProfessionalProfileEntity>> = profileDao.getPendingProfilesFlow()
    val allProfilesFlow: Flow<List<ProfessionalProfileEntity>> = profileDao.getAllProfilesFlow()

    fun getProfileByIdFlow(id: String): Flow<ProfessionalProfileEntity?> = profileDao.getProfileByIdFlow(id)
    suspend fun getProfileById(id: String): ProfessionalProfileEntity? = profileDao.getProfileById(id)
    fun getProfileByUserIdFlow(userId: String): Flow<ProfessionalProfileEntity?> = profileDao.getProfileByUserIdFlow(userId)
    suspend fun getProfileByUserId(userId: String): ProfessionalProfileEntity? = profileDao.getProfileByUserId(userId)

    suspend fun saveProfileDraft(profile: ProfessionalProfileEntity) {
        profileDao.insertProfile(profile)
    }

    suspend fun submitProfile(profile: ProfessionalProfileEntity) {
        val submitted = profile.copy(
            verificationStatus = "PENDING",
            publicationStatus = "PUBLISHED",
            updatedAt = System.currentTimeMillis()
        )
        profileDao.insertProfile(submitted)
    }

    suspend fun updateProfile(profile: ProfessionalProfileEntity) {
        profileDao.updateProfile(profile)
    }

    suspend fun deleteProfile(profile: ProfessionalProfileEntity) {
        profileDao.deleteProfile(profile)
    }

    // --- Search Engine ---
    fun searchApprovedProfiles(
        query: String,
        countryCode: String,
        department: String = "",
        city: String,
        neighborhood: String,
        categoryId: String
    ): Flow<List<ProfessionalProfileEntity>> {
        return combine(approvedProfilesFlow, categoryDao.getActiveCategoriesFlow()) { profiles, _ ->
            profiles.filter { pro ->
                val matchesCountry = countryCode.isBlank() || pro.countryCode.equals(countryCode, ignoreCase = true)
                val matchesDepartment = department.isBlank() || normalize(pro.department).contains(normalize(department))
                val matchesCity = city.isBlank() || pro.city.equals(city, ignoreCase = true)
                val matchesNeighborhood = neighborhood.isBlank() ||
                    normalize(pro.neighborhood).contains(normalize(neighborhood)) ||
                    normalize(pro.interventionZones).contains(normalize(neighborhood))
                val matchesCategory = categoryId.isBlank() || pro.primaryCategoryId == categoryId
                val matchesQuery = query.isBlank() ||
                    normalize(pro.businessName).contains(normalize(query)) ||
                    normalize(pro.description).contains(normalize(query)) ||
                    normalize(pro.department).contains(normalize(query)) ||
                    normalize(pro.city).contains(normalize(query)) ||
                    normalize(pro.neighborhood).contains(normalize(query)) ||
                    normalize(pro.interventionZones).contains(normalize(query))

                matchesCountry && matchesDepartment && matchesCity && matchesNeighborhood && matchesCategory && matchesQuery
            }
        }
    }

    private fun normalize(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
    }

    // --- Contact Events & Views ---
    suspend fun recordContactClick(profileId: String, eventType: String, sourcePage: String = "PROFILE") {
        contactEventDao.insertEvent(
            ContactEventEntity(
                id = "evt_" + UUID.randomUUID().toString(),
                professionalId = profileId,
                eventType = eventType,
                sourcePage = sourcePage
            )
        )
    }

    suspend fun recordProfileView(profileId: String) {
        profileDao.incrementViews(profileId)
        contactEventDao.insertEvent(
            ContactEventEntity(
                id = "evt_" + UUID.randomUUID().toString(),
                professionalId = profileId,
                eventType = "VIEW",
                sourcePage = "PROFILE"
            )
        )
    }

    fun getWhatsAppClicksCount(profileId: String): Flow<Int> = contactEventDao.getWhatsAppClicksCountFlow(profileId)
    fun getPhoneClicksCount(profileId: String): Flow<Int> = contactEventDao.getPhoneClicksCountFlow(profileId)
    fun getViewsCount(profileId: String): Flow<Int> = contactEventDao.getProfileViewsCountFlow(profileId)

    // --- Services ---
    fun getServicesForProfile(profileId: String): Flow<List<ServiceEntity>> = serviceDao.getServicesForProfileFlow(profileId)
    suspend fun addService(service: ServiceEntity) = serviceDao.insertService(service)
    suspend fun updateService(service: ServiceEntity) = serviceDao.updateService(service)
    suspend fun deleteService(serviceId: String) = serviceDao.deleteServiceById(serviceId)

    // --- Portfolio ---
    fun getPortfolioForProfile(profileId: String): Flow<List<PortfolioImageEntity>> = portfolioDao.getImagesForProfileFlow(profileId)
    suspend fun addPortfolioImage(image: PortfolioImageEntity) = portfolioDao.insertImage(image)
    suspend fun deletePortfolioImage(imageId: String) = portfolioDao.deleteImageById(imageId)

    // --- Reviews ---
    fun getApprovedReviews(profileId: String): Flow<List<ReviewEntity>> = reviewDao.getApprovedReviewsForProfileFlow(profileId)
    suspend fun submitReview(review: ReviewEntity) = reviewDao.insertReview(review)

    // --- Reports ---
    val allReportsFlow: Flow<List<ReportEntity>> = reportDao.getAllReportsFlow()
    suspend fun submitReport(report: ReportEntity) = reportDao.insertReport(report)
    suspend fun updateReportStatus(reportId: String, status: String, adminUserId: String) {
        reportDao.updateReportStatus(reportId, status, adminUserId, System.currentTimeMillis())
        auditDao.insertLog(
            AdminAuditLogEntity(
                id = "audit_" + UUID.randomUUID().toString().take(8),
                adminUserId = adminUserId,
                action = "RESOLVE_REPORT",
                targetType = "REPORT",
                targetId = reportId,
                details = "Signalement marqué comme $status"
            )
        )
    }

    // --- Subscriptions ---
    fun getSubscriptionForProfile(profileId: String): Flow<SubscriptionEntity?> = subscriptionDao.getSubscriptionForProfileFlow(profileId)
    suspend fun updateSubscription(sub: SubscriptionEntity) = subscriptionDao.updateSubscription(sub)

    // --- Categories ---
    val activeCategoriesFlow: Flow<List<CategoryEntity>> = categoryDao.getActiveCategoriesFlow()
    val allCategoriesFlow: Flow<List<CategoryEntity>> = categoryDao.getAllCategoriesFlow()
    suspend fun toggleCategory(categoryId: String, isActive: Boolean, adminUserId: String) {
        categoryDao.setCategoryActive(categoryId, isActive)
        auditDao.insertLog(
            AdminAuditLogEntity(
                id = "audit_" + UUID.randomUUID().toString().take(8),
                adminUserId = adminUserId,
                action = "TOGGLE_CATEGORY",
                targetType = "CATEGORY",
                targetId = categoryId,
                details = "Catégorie définie comme ${if (isActive) "active" else "inactive"}"
            )
        )
    }
    suspend fun addCategory(category: CategoryEntity, adminUserId: String) {
        categoryDao.insertCategory(category)
        auditDao.insertLog(
            AdminAuditLogEntity(
                id = "audit_" + UUID.randomUUID().toString().take(8),
                adminUserId = adminUserId,
                action = "ADD_CATEGORY",
                targetType = "CATEGORY",
                targetId = category.id,
                details = "Nouvelle catégorie créée : ${category.name}"
            )
        )
    }

    // --- Admin Operations ---
    val auditLogsFlow: Flow<List<AdminAuditLogEntity>> = auditDao.getAllLogsFlow()

    suspend fun approveProfile(profileId: String, adminUserId: String) {
        profileDao.updateVerificationStatus(profileId, "APPROVED", null, System.currentTimeMillis())
        auditDao.insertLog(
            AdminAuditLogEntity(
                id = "audit_" + UUID.randomUUID().toString().take(8),
                adminUserId = adminUserId,
                action = "APPROVE_PROFILE",
                targetType = "PROFESSIONAL_PROFILE",
                targetId = profileId,
                details = "Profil approuvé et mis en ligne avec succès."
            )
        )
    }

    suspend fun rejectProfile(profileId: String, reason: String, adminUserId: String) {
        profileDao.updateVerificationStatus(profileId, "REJECTED", reason, System.currentTimeMillis())
        auditDao.insertLog(
            AdminAuditLogEntity(
                id = "audit_" + UUID.randomUUID().toString().take(8),
                adminUserId = adminUserId,
                action = "REJECT_PROFILE",
                targetType = "PROFESSIONAL_PROFILE",
                targetId = profileId,
                details = "Profil rejeté pour le motif : $reason"
            )
        )
    }

    suspend fun suspendProfile(profileId: String, reason: String, adminUserId: String) {
        profileDao.updateVerificationStatus(profileId, "SUSPENDED", reason, System.currentTimeMillis())
        auditDao.insertLog(
            AdminAuditLogEntity(
                id = "audit_" + UUID.randomUUID().toString().take(8),
                adminUserId = adminUserId,
                action = "SUSPEND_PROFILE",
                targetType = "PROFESSIONAL_PROFILE",
                targetId = profileId,
                details = "Profil suspendu : $reason"
            )
        )
    }
}

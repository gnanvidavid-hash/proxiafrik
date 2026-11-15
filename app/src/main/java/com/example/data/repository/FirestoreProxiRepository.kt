package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AdminAuditLogRecord
import com.example.data.model.CategoryItem
import com.example.data.model.GeoCatalog
import com.example.data.model.PortfolioPhoto
import com.example.data.model.ProfessionalProfile
import com.example.data.model.ReportRecord
import com.example.data.model.ReviewItem
import com.example.data.model.ServiceItem
import com.example.data.model.UserAccount
import com.example.data.model.toAdminAuditLogRecord
import com.example.data.model.toCategoryItem
import com.example.data.model.toPortfolioPhoto
import com.example.data.model.toProfessionalProfile
import com.example.data.model.toReportRecord
import com.example.data.model.toReviewItem
import com.example.data.model.toServiceItem
import com.example.data.model.toUserAccount
import com.example.data.remote.OperationType
import com.example.data.remote.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.Normalizer
import java.util.UUID

class FirestoreProxiRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        db = FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        auth = Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Authentification requise avant d'accéder à Firestore.")
    }

    fun isBootstrappedAdmin(): Boolean {
        val user = auth.currentUser ?: return false
        return user.email == BOOTSTRAPPED_ADMIN_EMAIL && user.isEmailVerified
    }

    // --- Users & Role ---

    fun observeUserAccount(uid: String): Flow<UserAccount?> {
        val docRef = db.collection(COLLECTION_USERS).document(uid)
        return docRef.snapshots()
            .map { snapshot -> snapshot.toUserAccount() }
            .catch { error ->
                logFlowError(error, OperationType.GET, docRef.path, uid)
                throw error
            }
    }

    fun observeIsAdmin(uid: String): Flow<Boolean> {
        val adminDocRef = db.collection(COLLECTION_ADMINS).document(uid)
        return adminDocRef.snapshots()
            .map { snapshot -> snapshot.exists() || isBootstrappedAdmin() }
            .catch { error ->
                if (error is CancellationException &&
                    generateSequence<Throwable>(error) { it.cause }.none { it is FirebaseFirestoreException }
                ) {
                    throw error
                }
                emit(isBootstrappedAdmin())
            }
    }

    suspend fun ensureUserAccount(
        firebaseUser: FirebaseUser,
        preferredRole: String = "client"
    ): Result<UserAccount> {
        val docRef = db.collection(COLLECTION_USERS).document(firebaseUser.uid)
        return try {
            val existing = docRef.get().await().toUserAccount()
            if (existing != null) {
                if (isBootstrappedAdmin() && existing.role != "admin") {
                    docRef.update(
                        mapOf(
                            "role" to "admin",
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                    return Result.success(existing.copy(role = "admin"))
                }
                return Result.success(existing)
            }

            val sanitizedRole = when {
                isBootstrappedAdmin() -> "admin"
                preferredRole == "pro" -> "pro"
                else -> "client"
            }
            val displayName = firebaseUser.displayName?.takeIf { it.isNotBlank() }
                ?: firebaseUser.email?.substringBefore("@")
                ?: "Utilisateur ProxiAfrik"

            val newAccount = UserAccount(
                id = firebaseUser.uid,
                userId = firebaseUser.uid,
                fullName = displayName.take(100),
                email = (firebaseUser.email ?: "").take(150),
                phone = (firebaseUser.phoneNumber ?: "").take(30),
                role = sanitizedRole,
                accountStatus = "ACTIVE"
            )
            docRef.set(newAccount.toCreateMap()).await()
            val saved = docRef.get().await().toUserAccount() ?: newAccount
            Result.success(saved)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateOwnRole(role: String): Result<Unit> {
        val uid = requireUserId()
        val safeRole = if (role == "pro") "pro" else "client"
        val docRef = db.collection(COLLECTION_USERS).document(uid)
        return try {
            docRef.update(
                mapOf(
                    "role" to safeRole,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Categories ---

    fun observeCategories(): Flow<List<CategoryItem>> {
        val uid = requireUserId()
        val colRef = db.collection(COLLECTION_CATEGORIES)
        return colRef.snapshots()
            .map { snapshot ->
                val remote = snapshot.documents
                    .mapNotNull { it.toCategoryItem() }
                    .sortedBy { it.sortOrder }
                if (remote.isEmpty()) GeoCatalog.defaultCategories else remote
            }
            .catch { error ->
                logFlowError(error, OperationType.LIST, COLLECTION_CATEGORIES, uid)
                throw error
            }
    }

    suspend fun addCategory(name: String, description: String, iconKey: String, sortOrder: Int): Result<String> {
        val adminUid = requireUserId()
        val catId = "cat_" + UUID.randomUUID().toString().replace("-", "").take(10)
        val docRef = db.collection(COLLECTION_CATEGORIES).document(catId)
        val slug = normalize(name).replace("[^a-z0-9]+".toRegex(), "-").trim('-').ifBlank { catId }
        val category = CategoryItem(
            id = catId,
            name = name.trim().take(80),
            slug = slug.take(80),
            description = description.trim().take(300),
            iconKey = iconKey.ifBlank { "work" },
            isActive = true,
            sortOrder = sortOrder.coerceIn(0, 1000)
        )
        return try {
            docRef.set(category.toCreateMap()).await()
            writeAuditLog(
                adminUid = adminUid,
                action = "ADD_CATEGORY",
                targetType = "CATEGORY",
                targetId = catId,
                details = "Nouvelle catégorie créée : ${category.name}"
            )
            Result.success(catId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun toggleCategory(category: CategoryItem, newActive: Boolean): Result<Unit> {
        val adminUid = requireUserId()
        val docRef = db.collection(COLLECTION_CATEGORIES).document(category.id)
        return try {
            val snap = docRef.get().await()
            if (!snap.exists()) {
                docRef.set(category.copy(isActive = newActive).toCreateMap()).await()
            } else {
                docRef.update(
                    mapOf(
                        "isActive" to newActive,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            writeAuditLog(
                adminUid = adminUid,
                action = "TOGGLE_CATEGORY",
                targetType = "CATEGORY",
                targetId = category.id,
                details = "Catégorie ${category.name} définie comme ${if (newActive) "active" else "désactivée"}"
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Professionals Queries ---

    fun observeApprovedProfiles(): Flow<List<ProfessionalProfile>> {
        val uid = auth.currentUser?.uid ?: ""
        val query = db.collection(COLLECTION_PROFESSIONALS)
            .whereEqualTo("verificationStatus", "APPROVED")
            .whereEqualTo("publicationStatus", "PUBLISHED")
        return query.snapshots()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { it.toProfessionalProfile() }
                    .sortedByDescending { it.isSponsored }
            }
            .catch { error ->
                logFlowError(error, OperationType.LIST, COLLECTION_PROFESSIONALS, uid)
                throw error
            }
    }

    fun searchApprovedProfiles(
        queryText: String,
        countryCode: String,
        department: String,
        city: String,
        neighborhood: String,
        categoryId: String
    ): Flow<List<ProfessionalProfile>> {
        return combine(observeApprovedProfiles(), observeCategories()) { profiles, categories ->
            val catNameById = categories.associate { it.id to normalize(it.name) }
            val normQuery = normalize(queryText.trim())
            val normDept = normalize(department.trim())
            val normNeighborhood = normalize(neighborhood.trim())

            profiles.filter { pro ->
                val matchesCountry = countryCode.isBlank() || pro.countryCode.equals(countryCode, ignoreCase = true)
                val matchesDepartment = normDept.isBlank() || normalize(pro.department).contains(normDept)
                val matchesCity = city.isBlank() || pro.city.equals(city, ignoreCase = true)
                val matchesNeighborhood = normNeighborhood.isBlank() ||
                    normalize(pro.neighborhood).contains(normNeighborhood) ||
                    normalize(pro.interventionZones).contains(normNeighborhood)
                val matchesCategory = categoryId.isBlank() || pro.primaryCategoryId == categoryId
                val matchesQuery = normQuery.isBlank() ||
                    normalize(pro.businessName).contains(normQuery) ||
                    normalize(pro.description).contains(normQuery) ||
                    normalize(pro.department).contains(normQuery) ||
                    normalize(pro.city).contains(normQuery) ||
                    normalize(pro.neighborhood).contains(normQuery) ||
                    normalize(pro.interventionZones).contains(normQuery) ||
                    (catNameById[pro.primaryCategoryId]?.contains(normQuery) == true)

                matchesCountry && matchesDepartment && matchesCity && matchesNeighborhood && matchesCategory && matchesQuery
            }
        }
    }

    fun observeMyProfile(): Flow<ProfessionalProfile?> {
        val uid = requireUserId()
        val query = db.collection(COLLECTION_PROFESSIONALS)
            .whereEqualTo("userId", uid)
        return query.snapshots()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { it.toProfessionalProfile() }
                    .maxByOrNull { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                logFlowError(error, OperationType.LIST, COLLECTION_PROFESSIONALS, uid)
                throw error
            }
    }

    fun observeProfileById(profileId: String): Flow<ProfessionalProfile?> {
        val uid = requireUserId()
        val docRef = db.collection(COLLECTION_PROFESSIONALS).document(profileId)
        return docRef.snapshots()
            .map { it.toProfessionalProfile() }
            .catch { error ->
                logFlowError(error, OperationType.GET, docRef.path, uid)
                throw error
            }
    }

    fun observePendingProfilesForAdmin(): Flow<List<ProfessionalProfile>> {
        val uid = requireUserId()
        val query = db.collection(COLLECTION_PROFESSIONALS)
            .whereEqualTo("verificationStatus", "PENDING")
        return query.snapshots()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { it.toProfessionalProfile() }
                    .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                logFlowError(error, OperationType.LIST, COLLECTION_PROFESSIONALS, uid)
                throw error
            }
    }

    fun observeAllProfilesForAdmin(): Flow<List<ProfessionalProfile>> {
        val uid = requireUserId()
        val colRef = db.collection(COLLECTION_PROFESSIONALS)
        return colRef.snapshots()
            .map { snapshot ->
                snapshot.documents
                    .mapNotNull { it.toProfessionalProfile() }
                    .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                logFlowError(error, OperationType.LIST, COLLECTION_PROFESSIONALS, uid)
                throw error
            }
    }

    // --- Professionals Writes ---

    suspend fun submitProfile(
        profile: ProfessionalProfile,
        services: List<ServiceItem>,
        photos: List<PortfolioPhoto>
    ): Result<String> {
        val uid = requireUserId()
        val profileId = profile.id.ifBlank {
            "pro_" + UUID.randomUUID().toString().replace("-", "").take(12)
        }
        val docRef = db.collection(COLLECTION_PROFESSIONALS).document(profileId)
        val slug = normalize(profile.businessName)
            .replace("[^a-z0-9]+".toRegex(), "-")
            .trim('-')
            .ifBlank { profileId }

        val prepared = profile.copy(
            id = profileId,
            userId = uid,
            slug = slug.take(140),
            verificationStatus = "PENDING",
            publicationStatus = "PUBLISHED",
            rejectionReason = null,
            viewsCount = 0L,
            whatsappClicksCount = 0L,
            phoneClicksCount = 0L,
            isSponsored = false
        )

        return try {
            val existing = docRef.get().await()
            if (existing.exists()) {
                docRef.update(prepared.toOwnerUpdateMap()).await()
            } else {
                docRef.set(prepared.toCreateMap()).await()
            }

            if (!isBootstrappedAdmin()) {
                db.collection(COLLECTION_USERS).document(uid).update(
                    mapOf(
                        "role" to "pro",
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            services.forEach { srv ->
                val srvId = "srv_" + UUID.randomUUID().toString().replace("-", "").take(10)
                val srvRef = docRef.collection(SUBCOLLECTION_SERVICES).document(srvId)
                val preparedSrv = srv.copy(
                    id = srvId,
                    professionalId = profileId,
                    ownerUserId = uid,
                    categoryId = prepared.primaryCategoryId,
                    isActive = true
                )
                srvRef.set(preparedSrv.toCreateMap()).await()
            }

            photos.forEach { photo ->
                val imgId = "img_" + UUID.randomUUID().toString().replace("-", "").take(10)
                val imgRef = docRef.collection(SUBCOLLECTION_PORTFOLIO).document(imgId)
                val preparedPhoto = photo.copy(
                    id = imgId,
                    professionalId = profileId,
                    ownerUserId = uid,
                    visible = true
                )
                imgRef.set(preparedPhoto.toCreateMap()).await()
            }

            Result.success(profileId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun incrementProfileCounter(profileId: String, field: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection(COLLECTION_PROFESSIONALS).document(profileId)
        return try {
            docRef.update(field, FieldValue.increment(1L)).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Services, Portfolio & Reviews ---

    fun observeServicesForProfile(profileId: String): Flow<List<ServiceItem>> {
        val uid = auth.currentUser?.uid ?: ""
        val query = db.collection(COLLECTION_PROFESSIONALS)
            .document(profileId)
            .collection(SUBCOLLECTION_SERVICES)
            .whereEqualTo("isActive", true)
        return query.snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc -> doc.toServiceItem() }
            }
            .catch { error ->
                logFlowError(error, OperationType.LIST, "professionals/$profileId/services", uid)
                throw error
            }
    }

    suspend fun addService(profileId: String, categoryId: String, title: String, description: String, price: Double, pricingType: String): Result<Unit> {
        val uid = requireUserId()
        val srvId = "srv_" + UUID.randomUUID().toString().replace("-", "").take(10)
        val docRef = db.collection(COLLECTION_PROFESSIONALS)
            .document(profileId)
            .collection(SUBCOLLECTION_SERVICES)
            .document(srvId)
        val item = ServiceItem(
            id = srvId,
            professionalId = profileId,
            ownerUserId = uid,
            categoryId = categoryId,
            title = title.trim().take(120),
            description = description.trim().take(500),
            indicativePrice = price.coerceAtLeast(0.0),
            currencyCode = "FCFA",
            pricingType = pricingType,
            isActive = true
        )
        return try {
            docRef.set(item.toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteService(profileId: String, serviceId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection(COLLECTION_PROFESSIONALS)
            .document(profileId)
            .collection(SUBCOLLECTION_SERVICES)
            .document(serviceId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    fun observePortfolioForProfile(profileId: String): Flow<List<PortfolioPhoto>> {
        val uid = auth.currentUser?.uid ?: ""
        val query = db.collection(COLLECTION_PROFESSIONALS)
            .document(profileId)
            .collection(SUBCOLLECTION_PORTFOLIO)
            .whereEqualTo("visible", true)
        return query.snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc -> doc.toPortfolioPhoto() }
                    .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                logFlowError(error, OperationType.LIST, "professionals/$profileId/portfolio", uid)
                throw error
            }
    }

    suspend fun addPortfolioPhoto(profileId: String, imageUrl: String, caption: String): Result<Unit> {
        val uid = requireUserId()
        val imgId = "img_" + UUID.randomUUID().toString().replace("-", "").take(10)
        val docRef = db.collection(COLLECTION_PROFESSIONALS)
            .document(profileId)
            .collection(SUBCOLLECTION_PORTFOLIO)
            .document(imgId)
        val photo = PortfolioPhoto(
            id = imgId,
            professionalId = profileId,
            ownerUserId = uid,
            imageUrl = imageUrl,
            caption = caption.trim().take(200),
            visible = true
        )
        return try {
            docRef.set(photo.toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deletePortfolioPhoto(profileId: String, imageId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection(COLLECTION_PROFESSIONALS)
            .document(profileId)
            .collection(SUBCOLLECTION_PORTFOLIO)
            .document(imageId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeApprovedReviews(profileId: String): Flow<List<ReviewItem>> {
        val uid = auth.currentUser?.uid ?: ""
        val query = db.collection(COLLECTION_PROFESSIONALS)
            .document(profileId)
            .collection(SUBCOLLECTION_REVIEWS)
            .whereEqualTo("moderationStatus", "APPROVED")
        return query.snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc -> doc.toReviewItem() }
                    .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                logFlowError(error, OperationType.LIST, "professionals/$profileId/reviews", uid)
                throw error
            }
    }

    suspend fun submitReview(profileId: String, rating: Int, comment: String, authorName: String): Result<Unit> {
        val uid = requireUserId()
        val revId = "rev_" + UUID.randomUUID().toString().replace("-", "").take(10)
        val docRef = db.collection(COLLECTION_PROFESSIONALS)
            .document(profileId)
            .collection(SUBCOLLECTION_REVIEWS)
            .document(revId)
        val review = ReviewItem(
            id = revId,
            professionalId = profileId,
            authorUserId = uid,
            authorName = authorName.trim().ifBlank { "Client" }.take(80),
            rating = rating.coerceIn(1, 5),
            comment = comment.trim().take(1000),
            moderationStatus = "APPROVED"
        )
        return try {
            docRef.set(review.toCreateMap()).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Reports ---

    suspend fun submitReport(
        profileId: String,
        reason: String,
        description: String,
        reporterName: String
    ): Result<String> {
        val uid = requireUserId()
        val reportId = "rep_" + UUID.randomUUID().toString().replace("-", "").take(10)
        val docRef = db.collection(COLLECTION_REPORTS).document(reportId)
        val report = ReportRecord(
            id = reportId,
            reporterUserId = uid,
            reporterName = reporterName.trim().ifBlank { "Utilisateur" }.take(100),
            reportedProfileId = profileId,
            reason = reason.trim().take(120),
            description = description.trim().take(1000),
            status = "PENDING"
        )
        return try {
            docRef.set(report.toCreateMap()).await()
            Result.success(reportId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeAllReportsForAdmin(): Flow<List<ReportRecord>> {
        val uid = auth.currentUser?.uid ?: ""
        val colRef = db.collection(COLLECTION_REPORTS)
        return colRef.snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc -> doc.toReportRecord() }
                    .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                logFlowError(error, OperationType.LIST, COLLECTION_REPORTS, uid)
                throw error
            }
    }

    suspend fun updateReportStatus(reportId: String, status: String): Result<Unit> {
        val adminUid = requireUserId()
        val docRef = db.collection(COLLECTION_REPORTS).document(reportId)
        return try {
            docRef.update(
                mapOf(
                    "status" to status,
                    "reviewedBy" to adminUid,
                    "reviewedAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            writeAuditLog(
                adminUid = adminUid,
                action = "RESOLVE_REPORT",
                targetType = "REPORT",
                targetId = reportId,
                details = "Signalement traité avec le statut $status"
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // --- Admin Moderation ---

    fun observeAuditLogsForAdmin(): Flow<List<AdminAuditLogRecord>> {
        val uid = auth.currentUser?.uid ?: ""
        val colRef = db.collection(COLLECTION_AUDIT_LOGS)
        return colRef.snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc -> doc.toAdminAuditLogRecord() }
                    .sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                logFlowError(error, OperationType.LIST, COLLECTION_AUDIT_LOGS, uid)
                throw error
            }
    }

    suspend fun approveProfile(profileId: String): Result<Unit> {
        val adminUid = requireUserId()
        val docRef = db.collection(COLLECTION_PROFESSIONALS).document(profileId)
        return try {
            docRef.update(
                mapOf(
                    "verificationStatus" to "APPROVED",
                    "rejectionReason" to FieldValue.delete(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            writeAuditLog(
                adminUid = adminUid,
                action = "APPROVE_PROFILE",
                targetType = "PROFESSIONAL_PROFILE",
                targetId = profileId,
                details = "Profil validé et publié dans l'annuaire."
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun rejectProfile(profileId: String, reason: String): Result<Unit> {
        val adminUid = requireUserId()
        val docRef = db.collection(COLLECTION_PROFESSIONALS).document(profileId)
        return try {
            docRef.update(
                mapOf(
                    "verificationStatus" to "REJECTED",
                    "rejectionReason" to reason.trim().take(300),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            writeAuditLog(
                adminUid = adminUid,
                action = "REJECT_PROFILE",
                targetType = "PROFESSIONAL_PROFILE",
                targetId = profileId,
                details = "Profil refusé : ${reason.trim()}"
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun suspendProfile(profileId: String, reason: String): Result<Unit> {
        val adminUid = requireUserId()
        val docRef = db.collection(COLLECTION_PROFESSIONALS).document(profileId)
        return try {
            docRef.update(
                mapOf(
                    "verificationStatus" to "SUSPENDED",
                    "rejectionReason" to reason.trim().take(300),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            writeAuditLog(
                adminUid = adminUid,
                action = "SUSPEND_PROFILE",
                targetType = "PROFESSIONAL_PROFILE",
                targetId = profileId,
                details = "Profil suspendu : ${reason.trim()}"
            )
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    private suspend fun writeAuditLog(
        adminUid: String,
        action: String,
        targetType: String,
        targetId: String,
        details: String
    ) {
        val logId = "audit_" + UUID.randomUUID().toString().replace("-", "").take(12)
        val docRef = db.collection(COLLECTION_AUDIT_LOGS).document(logId)
        val log = AdminAuditLogRecord(
            id = logId,
            adminUserId = adminUid,
            action = action,
            targetType = targetType,
            targetId = targetId,
            details = details.take(500)
        )
        docRef.set(log.toCreateMap()).await()
    }

    private fun logFlowError(error: Throwable, op: OperationType, path: String, expectedUid: String) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        val toLog = firestoreError ?: (error as? Exception)
        if (toLog != null && auth.currentUser?.uid == expectedUid) {
            handleFirestoreError(toLog, op, path)
        }
    }

    private fun normalize(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
    }

    companion object {
        const val BOOTSTRAPPED_ADMIN_EMAIL = "gnanvidavid@gmail.com"
        const val COLLECTION_USERS = "users"
        const val COLLECTION_ADMINS = "admins"
        const val COLLECTION_CATEGORIES = "categories"
        const val COLLECTION_PROFESSIONALS = "professionals"
        const val SUBCOLLECTION_SERVICES = "services"
        const val SUBCOLLECTION_PORTFOLIO = "portfolio"
        const val SUBCOLLECTION_REVIEWS = "reviews"
        const val COLLECTION_REPORTS = "reports"
        const val COLLECTION_AUDIT_LOGS = "audit_logs"
    }
}

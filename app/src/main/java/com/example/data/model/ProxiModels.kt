package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserAccount(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "client", // "client", "pro", "admin"
    val accountStatus: String = "ACTIVE",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "fullName" to fullName,
        "email" to email,
        "phone" to phone,
        "role" to role,
        "accountStatus" to accountStatus,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class CategoryItem(
    @DocumentId val id: String = "",
    val name: String = "",
    val slug: String = "",
    val description: String = "",
    val iconKey: String = "work",
    @get:PropertyName("isActive") @set:PropertyName("isActive")
    var isActive: Boolean = true,
    val sortOrder: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "name" to name,
        "slug" to slug,
        "description" to description,
        "iconKey" to iconKey,
        "isActive" to isActive,
        "sortOrder" to sortOrder,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class ProfessionalProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val ownerName: String = "",
    val businessName: String = "",
    val slug: String = "",
    val description: String = "",
    val countryCode: String = "BJ",
    val countryName: String = "Bénin",
    val department: String = "",
    val city: String = "",
    val neighborhood: String = "",
    val interventionZones: String = "",
    val phonePublic: String = "",
    val whatsappNumber: String = "",
    val profileImage: String = "",
    val primaryCategoryId: String = "",
    val verificationStatus: String = "PENDING", // "PENDING", "APPROVED", "REJECTED", "SUSPENDED"
    val rejectionReason: String? = null,
    val publicationStatus: String = "PUBLISHED",
    val openingHours: String = "Lun - Sam : 08h00 - 18h30",
    val consentGiven: Boolean = false,
    val viewsCount: Long = 0L,
    val whatsappClicksCount: Long = 0L,
    val phoneClicksCount: Long = 0L,
    @get:PropertyName("isSponsored") @set:PropertyName("isSponsored")
    var isSponsored: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> {
        val raw = mutableMapOf<String, Any?>(
            "userId" to userId,
            "ownerName" to ownerName,
            "businessName" to businessName,
            "slug" to slug,
            "description" to description,
            "countryCode" to countryCode,
            "countryName" to countryName,
            "department" to department,
            "city" to city,
            "neighborhood" to neighborhood,
            "interventionZones" to interventionZones,
            "phonePublic" to phonePublic,
            "whatsappNumber" to whatsappNumber,
            "profileImage" to profileImage,
            "primaryCategoryId" to primaryCategoryId,
            "verificationStatus" to "PENDING",
            "rejectionReason" to rejectionReason,
            "publicationStatus" to "PUBLISHED",
            "openingHours" to openingHours,
            "consentGiven" to consentGiven,
            "viewsCount" to 0L,
            "whatsappClicksCount" to 0L,
            "phoneClicksCount" to 0L,
            "isSponsored" to false,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }

    fun toOwnerUpdateMap(): Map<String, Any> = mapOf(
        "ownerName" to ownerName,
        "businessName" to businessName,
        "slug" to slug,
        "description" to description,
        "countryCode" to countryCode,
        "countryName" to countryName,
        "department" to department,
        "city" to city,
        "neighborhood" to neighborhood,
        "interventionZones" to interventionZones,
        "phonePublic" to phonePublic,
        "whatsappNumber" to whatsappNumber,
        "profileImage" to profileImage,
        "primaryCategoryId" to primaryCategoryId,
        "openingHours" to openingHours,
        "publicationStatus" to publicationStatus,
        "verificationStatus" to "PENDING",
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class ServiceItem(
    @DocumentId val id: String = "",
    val professionalId: String = "",
    val ownerUserId: String = "",
    val categoryId: String = "",
    val title: String = "",
    val description: String = "",
    val indicativePrice: Double = 0.0,
    val currencyCode: String = "FCFA",
    val pricingType: String = "SUR_DEVIS", // "SUR_DEVIS", "FIXED", "STARTING_AT"
    @get:PropertyName("isActive") @set:PropertyName("isActive")
    var isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "professionalId" to professionalId,
        "ownerUserId" to ownerUserId,
        "categoryId" to categoryId,
        "title" to title,
        "description" to description,
        "indicativePrice" to indicativePrice,
        "currencyCode" to currencyCode,
        "pricingType" to pricingType,
        "isActive" to isActive,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class PortfolioPhoto(
    @DocumentId val id: String = "",
    val professionalId: String = "",
    val ownerUserId: String = "",
    val imageUrl: String = "",
    val caption: String = "",
    val visible: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "professionalId" to professionalId,
        "ownerUserId" to ownerUserId,
        "imageUrl" to imageUrl,
        "caption" to caption,
        "visible" to true,
        "createdAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class ReviewItem(
    @DocumentId val id: String = "",
    val professionalId: String = "",
    val authorUserId: String = "",
    val authorName: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val moderationStatus: String = "APPROVED",
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "professionalId" to professionalId,
        "authorUserId" to authorUserId,
        "authorName" to authorName,
        "rating" to rating,
        "comment" to comment,
        "moderationStatus" to "APPROVED",
        "createdAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class ReportRecord(
    @DocumentId val id: String = "",
    val reporterUserId: String = "",
    val reporterName: String = "",
    val reportedProfileId: String = "",
    val reason: String = "",
    val description: String = "",
    val status: String = "PENDING", // "PENDING", "RESOLVED", "DISMISSED", "SUSPENDED"
    val reviewedBy: String? = null,
    val reviewedAt: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "reporterUserId" to reporterUserId,
        "reporterName" to reporterName,
        "reportedProfileId" to reportedProfileId,
        "reason" to reason,
        "description" to description,
        "status" to "PENDING",
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

@IgnoreExtraProperties
data class AdminAuditLogRecord(
    @DocumentId val id: String = "",
    val adminUserId: String = "",
    val action: String = "",
    val targetType: String = "",
    val targetId: String = "",
    val details: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "adminUserId" to adminUserId,
        "action" to action,
        "targetType" to targetType,
        "targetId" to targetId,
        "details" to details,
        "createdAt" to FieldValue.serverTimestamp()
    )
}

fun DocumentSnapshot.toProfessionalProfile(): ProfessionalProfile? {
    if (!exists()) return null
    return ProfessionalProfile(
        id = id,
        userId = getString("userId") ?: "",
        ownerName = getString("ownerName") ?: "",
        businessName = getString("businessName") ?: "",
        slug = getString("slug") ?: "",
        description = getString("description") ?: "",
        countryCode = getString("countryCode") ?: "BJ",
        countryName = getString("countryName") ?: "",
        department = getString("department") ?: "",
        city = getString("city") ?: "",
        neighborhood = getString("neighborhood") ?: "",
        interventionZones = getString("interventionZones") ?: "",
        phonePublic = getString("phonePublic") ?: "",
        whatsappNumber = getString("whatsappNumber") ?: "",
        profileImage = getString("profileImage") ?: "",
        primaryCategoryId = getString("primaryCategoryId") ?: "",
        verificationStatus = getString("verificationStatus") ?: "PENDING",
        rejectionReason = getString("rejectionReason"),
        publicationStatus = getString("publicationStatus") ?: "PUBLISHED",
        openingHours = getString("openingHours") ?: "",
        consentGiven = getBoolean("consentGiven") ?: false,
        viewsCount = getLong("viewsCount") ?: 0L,
        whatsappClicksCount = getLong("whatsappClicksCount") ?: 0L,
        phoneClicksCount = getLong("phoneClicksCount") ?: 0L,
        isSponsored = getBoolean("isSponsored") ?: false,
        createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
        updatedAt = getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
}

fun DocumentSnapshot.toCategoryItem(): CategoryItem? {
    if (!exists()) return null
    return CategoryItem(
        id = id,
        name = getString("name") ?: "",
        slug = getString("slug") ?: "",
        description = getString("description") ?: "",
        iconKey = getString("iconKey") ?: "work",
        isActive = getBoolean("isActive") ?: true,
        sortOrder = (getLong("sortOrder") ?: 0L).toInt(),
        createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
        updatedAt = getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
}

fun DocumentSnapshot.toUserAccount(): UserAccount? {
    if (!exists()) return null
    return UserAccount(
        id = id,
        userId = getString("userId") ?: id,
        fullName = getString("fullName") ?: "",
        email = getString("email") ?: "",
        phone = getString("phone") ?: "",
        role = getString("role") ?: "client",
        accountStatus = getString("accountStatus") ?: "ACTIVE",
        createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
        updatedAt = getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
}

fun DocumentSnapshot.toServiceItem(): ServiceItem? {
    if (!exists()) return null
    return ServiceItem(
        id = id,
        professionalId = getString("professionalId") ?: "",
        ownerUserId = getString("ownerUserId") ?: "",
        categoryId = getString("categoryId") ?: "",
        title = getString("title") ?: "",
        description = getString("description") ?: "",
        indicativePrice = getDouble("indicativePrice") ?: 0.0,
        currencyCode = getString("currencyCode") ?: "FCFA",
        pricingType = getString("pricingType") ?: "SUR_DEVIS",
        isActive = getBoolean("isActive") ?: true,
        createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
        updatedAt = getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
}

fun DocumentSnapshot.toPortfolioPhoto(): PortfolioPhoto? {
    if (!exists()) return null
    return PortfolioPhoto(
        id = id,
        professionalId = getString("professionalId") ?: "",
        ownerUserId = getString("ownerUserId") ?: "",
        imageUrl = getString("imageUrl") ?: "",
        caption = getString("caption") ?: "",
        visible = getBoolean("visible") ?: true,
        createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
}

fun DocumentSnapshot.toReviewItem(): ReviewItem? {
    if (!exists()) return null
    return ReviewItem(
        id = id,
        professionalId = getString("professionalId") ?: "",
        authorUserId = getString("authorUserId") ?: "",
        authorName = getString("authorName") ?: "",
        rating = (getLong("rating") ?: 5L).toInt(),
        comment = getString("comment") ?: "",
        moderationStatus = getString("moderationStatus") ?: "APPROVED",
        createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
}

fun DocumentSnapshot.toReportRecord(): ReportRecord? {
    if (!exists()) return null
    return ReportRecord(
        id = id,
        reporterUserId = getString("reporterUserId") ?: "",
        reporterName = getString("reporterName") ?: "",
        reportedProfileId = getString("reportedProfileId") ?: "",
        reason = getString("reason") ?: "",
        description = getString("description") ?: "",
        status = getString("status") ?: "PENDING",
        reviewedBy = getString("reviewedBy"),
        reviewedAt = getTimestamp("reviewedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
        createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE),
        updatedAt = getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
}

fun DocumentSnapshot.toAdminAuditLogRecord(): AdminAuditLogRecord? {
    if (!exists()) return null
    return AdminAuditLogRecord(
        id = id,
        adminUserId = getString("adminUserId") ?: "",
        action = getString("action") ?: "",
        targetType = getString("targetType") ?: "",
        targetId = getString("targetId") ?: "",
        details = getString("details") ?: "",
        createdAt = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    )
}

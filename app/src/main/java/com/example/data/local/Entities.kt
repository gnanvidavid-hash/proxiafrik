package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String, // "CLIENT", "PROFESSIONAL", "ADMIN"
    val accountStatus: String = "ACTIVE", // "ACTIVE", "SUSPENDED"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "professional_profiles")
data class ProfessionalProfileEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val businessName: String,
    val slug: String,
    val description: String,
    val countryCode: String, // "BJ", "TG", "CI", "SN"
    val countryName: String,
    val department: String = "",
    val city: String,
    val neighborhood: String,
    val interventionZones: String = "",
    val phonePublic: String,
    val whatsappNumber: String,
    val profileImage: String = "",
    val primaryCategoryId: String,
    val verificationStatus: String = "PENDING", // "PENDING", "APPROVED", "REJECTED", "SUSPENDED"
    val rejectionReason: String? = null,
    val publicationStatus: String = "PUBLISHED", // "DRAFT", "PUBLISHED", "ARCHIVED"
    val openingHours: String = "Lun - Sam : 08h00 - 18h00",
    val viewsCount: Int = 0,
    val isSponsored: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slug: String,
    val description: String,
    val iconKey: String, // e.g. "plumbing", "electrician", "mechanic"
    val isActive: Boolean = true,
    val sortOrder: Int = 0
)

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val professionalId: String,
    val categoryId: String,
    val title: String,
    val description: String,
    val indicativePrice: Double = 0.0,
    val currencyCode: String = "FCFA",
    val pricingType: String = "SUR_DEVIS", // "SUR_DEVIS", "FIXED", "STARTING_AT"
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "portfolio_images")
data class PortfolioImageEntity(
    @PrimaryKey val id: String,
    val professionalId: String,
    val imageUrl: String,
    val caption: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val professionalId: String,
    val authorUserId: String,
    val authorName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val moderationStatus: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val reporterUserId: String,
    val reporterName: String,
    val reportedProfileId: String,
    val reason: String,
    val description: String,
    val status: String = "PENDING", // "PENDING", "RESOLVED", "DISMISSED"
    val reviewedBy: String? = null,
    val reviewedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val professionalId: String,
    val planCode: String, // "FREE", "PRO", "VISIBILITY"
    val planName: String,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
    val status: String = "ACTIVE",
    val paymentReference: String = "OFFLINE_ACTIVATION",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "contact_events")
data class ContactEventEntity(
    @PrimaryKey val id: String,
    val professionalId: String,
    val eventType: String, // "WHATSAPP", "PHONE", "VIEW"
    val sourcePage: String = "PROFILE",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey val id: String,
    val adminUserId: String,
    val action: String, // "APPROVE_PROFILE", "REJECT_PROFILE", "SUSPEND_PROFILE", etc.
    val targetType: String,
    val targetId: String,
    val details: String,
    val createdAt: Long = System.currentTimeMillis()
)

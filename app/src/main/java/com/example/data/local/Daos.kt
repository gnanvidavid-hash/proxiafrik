package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface ProfessionalProfileDao {
    @Query("SELECT * FROM professional_profiles WHERE verificationStatus = 'APPROVED' AND publicationStatus = 'PUBLISHED' ORDER BY isSponsored DESC, viewsCount DESC, createdAt DESC")
    fun getApprovedProfilesFlow(): Flow<List<ProfessionalProfileEntity>>

    @Query("SELECT * FROM professional_profiles WHERE verificationStatus = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingProfilesFlow(): Flow<List<ProfessionalProfileEntity>>

    @Query("SELECT * FROM professional_profiles ORDER BY createdAt DESC")
    fun getAllProfilesFlow(): Flow<List<ProfessionalProfileEntity>>

    @Query("SELECT * FROM professional_profiles WHERE id = :id LIMIT 1")
    fun getProfileByIdFlow(id: String): Flow<ProfessionalProfileEntity?>

    @Query("SELECT * FROM professional_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfileById(id: String): ProfessionalProfileEntity?

    @Query("SELECT * FROM professional_profiles WHERE userId = :userId LIMIT 1")
    fun getProfileByUserIdFlow(userId: String): Flow<ProfessionalProfileEntity?>

    @Query("SELECT * FROM professional_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfileByUserId(userId: String): ProfessionalProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfessionalProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfessionalProfileEntity)

    @Query("UPDATE professional_profiles SET verificationStatus = :status, rejectionReason = :reason, updatedAt = :updatedAt WHERE id = :profileId")
    suspend fun updateVerificationStatus(profileId: String, status: String, reason: String?, updatedAt: Long)

    @Query("UPDATE professional_profiles SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: String)

    @Delete
    suspend fun deleteProfile(profile: ProfessionalProfileEntity)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY sortOrder ASC, name ASC")
    fun getActiveCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("UPDATE categories SET isActive = :isActive WHERE id = :id")
    suspend fun setCategoryActive(id: String, isActive: Boolean)
}

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services WHERE professionalId = :profileId AND isActive = 1 ORDER BY createdAt ASC")
    fun getServicesForProfileFlow(profileId: String): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE professionalId = :profileId ORDER BY createdAt ASC")
    suspend fun getServicesForProfile(profileId: String): List<ServiceEntity>

    @Query("SELECT COUNT(*) FROM services WHERE isActive = 1")
    fun getActiveServicesCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceEntity>)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Query("DELETE FROM services WHERE id = :id")
    suspend fun deleteServiceById(id: String)
}

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio_images WHERE professionalId = :profileId ORDER BY createdAt DESC")
    fun getImagesForProfileFlow(profileId: String): Flow<List<PortfolioImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: PortfolioImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<PortfolioImageEntity>)

    @Query("DELETE FROM portfolio_images WHERE id = :id")
    suspend fun deleteImageById(id: String)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE professionalId = :profileId AND moderationStatus = 'APPROVED' ORDER BY createdAt DESC")
    fun getApprovedReviewsForProfileFlow(profileId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviewsFlow(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<ReviewEntity>)

    @Query("UPDATE reviews SET moderationStatus = :status WHERE id = :id")
    suspend fun updateModerationStatus(id: String, status: String)
}

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReportsFlow(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("UPDATE reports SET status = :status, reviewedBy = :reviewedBy, reviewedAt = :reviewedAt WHERE id = :id")
    suspend fun updateReportStatus(id: String, status: String, reviewedBy: String, reviewedAt: Long)
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE professionalId = :profileId LIMIT 1")
    fun getSubscriptionForProfileFlow(profileId: String): Flow<SubscriptionEntity?>

    @Query("SELECT * FROM subscriptions WHERE professionalId = :profileId LIMIT 1")
    suspend fun getSubscriptionForProfile(profileId: String): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity)

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)
}

@Dao
interface ContactEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ContactEventEntity)

    @Query("SELECT COUNT(*) FROM contact_events WHERE professionalId = :profileId AND eventType = 'WHATSAPP'")
    fun getWhatsAppClicksCountFlow(profileId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM contact_events WHERE professionalId = :profileId AND eventType = 'PHONE'")
    fun getPhoneClicksCountFlow(profileId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM contact_events WHERE professionalId = :profileId AND eventType = 'VIEW'")
    fun getProfileViewsCountFlow(profileId: String): Flow<Int>
}

@Dao
interface AdminAuditLogDao {
    @Query("SELECT * FROM admin_audit_logs ORDER BY createdAt DESC")
    fun getAllLogsFlow(): Flow<List<AdminAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AdminAuditLogEntity)
}

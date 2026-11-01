package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ProfessionalProfileEntity::class,
        CategoryEntity::class,
        ServiceEntity::class,
        PortfolioImageEntity::class,
        ReviewEntity::class,
        ReportEntity::class,
        SubscriptionEntity::class,
        ContactEventEntity::class,
        AdminAuditLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun professionalProfileDao(): ProfessionalProfileDao
    abstract fun categoryDao(): CategoryDao
    abstract fun serviceDao(): ServiceDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun reviewDao(): ReviewDao
    abstract fun reportDao(): ReportDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun contactEventDao(): ContactEventDao
    abstract fun adminAuditLogDao(): AdminAuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "proxiafrik.db"
                )
                .addCallback(DatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                seedInitialData()
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                seedInitialData()
            }

            private fun seedInitialData() {
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        SeedData.populateInitialData(database)
                    }
                }
            }
        }
    }
}

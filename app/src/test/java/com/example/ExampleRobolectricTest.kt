package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.CategoryEntity
import com.example.data.local.ProfessionalProfileEntity
import com.example.data.local.ReportEntity
import com.example.data.local.SeedData
import com.example.data.local.ServiceEntity
import com.example.data.local.UserEntity
import com.example.data.repository.ProxiAfrikRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: ProxiAfrikRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        repository = ProxiAfrikRepository(
            userDao = database.userDao(),
            profileDao = database.professionalProfileDao(),
            categoryDao = database.categoryDao(),
            serviceDao = database.serviceDao(),
            portfolioDao = database.portfolioDao(),
            reviewDao = database.reviewDao(),
            reportDao = database.reportDao(),
            subscriptionDao = database.subscriptionDao(),
            contactEventDao = database.contactEventDao(),
            auditDao = database.adminAuditLogDao()
        )
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `verify app name resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ProxiAfrik", appName)
    }

    @Test
    fun `verify database seeding and user roles`() = runBlocking {
        SeedData.populateInitialData(database)

        // Verify users
        val client = database.userDao().getUserByEmail("client@proxiafrik.com")
        assertNotNull(client)
        assertEquals("CLIENT", client?.role)

        val pro = database.userDao().getUserByEmail("pro@proxiafrik.com")
        assertNotNull(pro)
        assertEquals("PROFESSIONAL", pro?.role)

        val admin = database.userDao().getUserByEmail("admin@proxiafrik.com")
        assertNotNull(admin)
        assertEquals("ADMIN", admin?.role)
    }

    @Test
    fun `verify search excludes pending and returns only approved profiles`() = runBlocking {
        SeedData.populateInitialData(database)

        // Initial search for Cotonou, Bénin
        val results = repository.searchApprovedProfiles(
            query = "",
            countryCode = "BJ",
            city = "Cotonou",
            neighborhood = "",
            categoryId = ""
        ).first()

        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.verificationStatus == "APPROVED" })

        // Ensure pending profile (Ivoire Tech Fix) is NOT in the public approved results
        val pendingInResults = results.any { it.id == "pro_pending_01" }
        assertFalse(pendingInResults)
    }

    @Test
    fun `verify search by department city and neighborhood filtering`() = runBlocking {
        SeedData.populateInitialData(database)

        // 1. Filter by Department Littoral (Cotonou)
        val littoralResults = repository.searchApprovedProfiles(
            query = "",
            countryCode = "BJ",
            department = "Littoral",
            city = "",
            neighborhood = "",
            categoryId = ""
        ).first()
        assertTrue(littoralResults.any { it.id == "pro_01" })
        assertFalse(littoralResults.any { it.id == "pro_04" })

        // 2. Filter by Department Atlantique (Abomey-Calavi)
        val atlantiqueResults = repository.searchApprovedProfiles(
            query = "",
            countryCode = "BJ",
            department = "Atlantique",
            city = "",
            neighborhood = "",
            categoryId = ""
        ).first()
        assertTrue(atlantiqueResults.any { it.id == "pro_04" })
        assertFalse(atlantiqueResults.any { it.id == "pro_01" })

        // 3. Filter by City Abomey-Calavi
        val calaviResults = repository.searchApprovedProfiles(
            query = "",
            countryCode = "BJ",
            department = "",
            city = "Abomey-Calavi",
            neighborhood = "",
            categoryId = ""
        ).first()
        assertEquals(1, calaviResults.size)
        assertEquals("pro_04", calaviResults.first().id)

        // 4. Filter by Neighborhood Godomey
        val godomeyResults = repository.searchApprovedProfiles(
            query = "",
            countryCode = "BJ",
            department = "",
            city = "",
            neighborhood = "Godomey",
            categoryId = ""
        ).first()
        assertTrue(godomeyResults.any { it.id == "pro_04" })
        assertFalse(godomeyResults.any { it.id == "pro_01" })

        // 5. Filter by Neighborhood Cadjehoun
        val cadjehounResults = repository.searchApprovedProfiles(
            query = "",
            countryCode = "BJ",
            department = "",
            city = "",
            neighborhood = "Cadjehoun",
            categoryId = ""
        ).first()
        assertTrue(cadjehounResults.any { it.id == "pro_01" })
        assertFalse(cadjehounResults.any { it.id == "pro_04" })

        // 6. Full text query matching department name
        val queryDeptResults = repository.searchApprovedProfiles(
            query = "Littoral",
            countryCode = "",
            department = "",
            city = "",
            neighborhood = "",
            categoryId = ""
        ).first()
        assertTrue(queryDeptResults.any { it.id == "pro_01" })
    }

    @Test
    fun `verify all countries have complete departments and cities`() {
        val countries = SeedData.supportedCountries
        assertTrue(countries.size >= 5)

        val benin = countries.find { it.code == "BJ" }
        assertNotNull(benin)
        assertEquals(12, benin?.departments?.size)
        assertTrue(benin!!.departments.all { it.cities.isNotEmpty() })

        val togo = countries.find { it.code == "TG" }
        assertNotNull(togo)
        assertEquals(5, togo?.departments?.size)

        val senegal = countries.find { it.code == "SN" }
        assertNotNull(senegal)
        assertEquals(14, senegal?.departments?.size)

        val cameroon = countries.find { it.code == "CM" }
        assertNotNull(cameroon)
        assertEquals(10, cameroon?.departments?.size)

        val coteIvoire = countries.find { it.code == "CI" }
        assertNotNull(coteIvoire)
        assertTrue((coteIvoire?.departments?.size ?: 0) >= 30)
    }

    @Test
    fun `verify admin validation approval and audit logging`() = runBlocking {
        SeedData.populateInitialData(database)

        val pendingListBefore = repository.pendingProfilesFlow.first()
        assertTrue(pendingListBefore.any { it.id == "pro_pending_01" })

        // Admin approves profile
        repository.approveProfile("pro_pending_01", "user_admin_01")

        val pendingListAfter = repository.pendingProfilesFlow.first()
        assertFalse(pendingListAfter.any { it.id == "pro_pending_01" })

        val approvedProfile = repository.getProfileById("pro_pending_01")
        assertEquals("APPROVED", approvedProfile?.verificationStatus)

        // Verify audit log entry was created
        val logs = repository.auditLogsFlow.first()
        assertTrue(logs.any { it.action == "APPROVE_PROFILE" && it.targetId == "pro_pending_01" })
    }

    @Test
    fun `verify admin rejection with mandatory reason`() = runBlocking {
        SeedData.populateInitialData(database)

        repository.rejectProfile("pro_pending_01", "Numéro de téléphone invalide après vérification", "user_admin_01")

        val rejectedProfile = repository.getProfileById("pro_pending_01")
        assertEquals("REJECTED", rejectedProfile?.verificationStatus)
        assertEquals("Numéro de téléphone invalide après vérification", rejectedProfile?.rejectionReason)
    }

    @Test
    fun `verify contact events and profile view incrementation`() = runBlocking {
        SeedData.populateInitialData(database)

        val proId = "pro_01"
        val initialWhatsAppClicks = repository.getWhatsAppClicksCount(proId).first()
        val initialPhoneClicks = repository.getPhoneClicksCount(proId).first()

        repository.recordContactClick(proId, "WHATSAPP")
        repository.recordContactClick(proId, "PHONE")

        val updatedWhatsAppClicks = repository.getWhatsAppClicksCount(proId).first()
        val updatedPhoneClicks = repository.getPhoneClicksCount(proId).first()

        assertEquals(initialWhatsAppClicks + 1, updatedWhatsAppClicks)
        assertEquals(initialPhoneClicks + 1, updatedPhoneClicks)
    }

    @Test
    fun `verify reporting profile workflow`() = runBlocking {
        SeedData.populateInitialData(database)

        val report = ReportEntity(
            id = "rep_test_01",
            reporterUserId = "user_client_01",
            reporterName = "Aïcha Dossou",
            reportedProfileId = "pro_01",
            reason = "Coordonnées erronées",
            description = "Le professionnel ne répond plus à cette adresse."
        )
        repository.submitReport(report)

        val reports = repository.allReportsFlow.first()
        assertTrue(reports.any { it.id == "rep_test_01" && it.status == "PENDING" })

        // Admin resolves report
        repository.updateReportStatus("rep_test_01", "RESOLVED", "user_admin_01")

        val reportsAfter = repository.allReportsFlow.first()
        val resolved = reportsAfter.first { it.id == "rep_test_01" }
        assertEquals("RESOLVED", resolved.status)
        assertEquals("user_admin_01", resolved.reviewedBy)
    }
}

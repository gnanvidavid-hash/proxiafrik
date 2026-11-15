package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GeoCatalog
import com.example.data.model.ProfessionalProfile
import com.example.data.model.ReportRecord
import com.example.data.model.UserAccount
import com.example.ui.viewmodel.SearchFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `verify app name resource`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ProxiAfrik", appName)
    }

    @Test
    fun `verify all countries have complete departments and cities`() {
        val countries = GeoCatalog.supportedCountries
        assertTrue(countries.size >= 10)

        val benin = countries.find { it.code == "BJ" }
        assertNotNull(benin)
        assertEquals(12, benin?.departments?.size)
        assertTrue(benin!!.departments.all { it.cities.isNotEmpty() })

        val togo = countries.find { it.code == "TG" }
        assertNotNull(togo)
        assertEquals(5, togo?.departments?.size)
        assertTrue(togo!!.departments.all { it.cities.isNotEmpty() })

        val senegal = countries.find { it.code == "SN" }
        assertNotNull(senegal)
        assertEquals(14, senegal?.departments?.size)
        assertTrue(senegal!!.departments.all { it.cities.isNotEmpty() })

        val cameroon = countries.find { it.code == "CM" }
        assertNotNull(cameroon)
        assertEquals(10, cameroon?.departments?.size)
        assertTrue(cameroon!!.departments.all { it.cities.isNotEmpty() })

        val coteIvoire = countries.find { it.code == "CI" }
        assertNotNull(coteIvoire)
        assertTrue((coteIvoire?.departments?.size ?: 0) >= 30)
        assertTrue(coteIvoire!!.departments.all { it.cities.isNotEmpty() })
    }

    @Test
    fun `verify default categories cover core service sectors`() {
        val categories = GeoCatalog.defaultCategories
        assertEquals(18, categories.size)
        assertTrue(categories.all { it.isActive })
        assertTrue(categories.any { it.id == "cat_plumbing" && it.name == "Plomberie" })
        assertTrue(categories.any { it.id == "cat_electricity" && it.name == "Électricité" })
    }

    @Test
    fun `verify professional profile creation and update always enforce PENDING status`() {
        val profile = ProfessionalProfile(
            id = "pro_123",
            userId = "uid_123",
            ownerName = "Koffi Mensah",
            businessName = "Atelier Mensah",
            description = "Plomberie générale",
            countryCode = "BJ",
            countryName = "Bénin",
            department = "Littoral",
            city = "Cotonou",
            neighborhood = "Cadjehoun",
            whatsappNumber = "+2290190000000",
            primaryCategoryId = "cat_plumbing",
            verificationStatus = "APPROVED", // Attempted client bypass must be stripped
            consentGiven = true
        )

        val createMap = profile.toCreateMap()
        assertEquals("PENDING", createMap["verificationStatus"])
        assertEquals("PUBLISHED", createMap["publicationStatus"])
        assertEquals(0L, createMap["viewsCount"])
        assertEquals(false, createMap["isSponsored"])

        val updateMap = profile.toOwnerUpdateMap()
        assertEquals("PENDING", updateMap["verificationStatus"])
    }

    @Test
    fun `verify default user role and report record defaults`() {
        val defaultUser = UserAccount(userId = "uid_1", fullName = "Amina")
        assertEquals("client", defaultUser.role)
        assertEquals("ACTIVE", defaultUser.accountStatus)

        val report = ReportRecord(
            reporterUserId = "uid_1",
            reporterName = "Amina",
            reportedProfileId = "pro_1",
            reason = "Coordonnées incorrectes",
            description = "Numéro injoignable"
        )
        val reportMap = report.toCreateMap()
        assertEquals("PENDING", reportMap["status"])
        assertFalse(reportMap.containsKey("reviewedBy"))
    }

    @Test
    fun `verify search filter defaults target Cotonou Benin`() {
        val filter = SearchFilter()
        assertEquals("BJ", filter.countryCode)
        assertEquals("Cotonou", filter.city)
        assertEquals("", filter.query)
        assertEquals("", filter.department)
        assertEquals("", filter.neighborhood)
        assertEquals("", filter.categoryId)
    }
}

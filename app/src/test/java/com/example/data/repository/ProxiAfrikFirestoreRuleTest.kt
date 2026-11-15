package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.PortfolioPhoto
import com.example.data.model.ProfessionalProfile
import com.example.data.model.ServiceItem
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class ProxiAfrikFirestoreRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun unauthenticated_observeApprovedProfiles_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = FirestoreProxiRepository(firestore, auth)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeApprovedProfiles().first()
            }
            fail("Expected PERMISSION_DENIED for unauthenticated caller")
        } catch (e: Exception) {
            val fsException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsException.code)
        }
    }

    @Test
    fun nonAdmin_cannotSelfAssignAdminRole_orReadReports() = runBlocking {
        val clientUid = signInTestUser("client_rbac_${UUID.randomUUID().toString().take(6)}@test.com")
        val repository = FirestoreProxiRepository(firestore, auth)
        val user = checkNotNull(auth.currentUser)

        val accountResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.ensureUserAccount(user, "client")
        }
        assertTrue(accountResult.isSuccess)
        assertEquals("client", accountResult.getOrThrow().role)

        // Attempt direct role elevation to admin -> rejected by firestore.rules
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                firestore.collection("users").document(clientUid)
                    .update("role", "admin")
                    .await()
            }
            fail("Expected PERMISSION_DENIED when non-admin elevates role to admin")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }

        // Attempt admin reports list -> rejected by firestore.rules
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeAllReportsForAdmin().first()
            }
            fail("Expected PERMISSION_DENIED when non-admin queries reports")
        } catch (e: Exception) {
            val fsException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsException.code)
        }
    }

    @Test
    fun proSubmission_enforcesPendingAndAdminApprovalWorkflow() = runBlocking {
        val proEmail = "pro_${UUID.randomUUID().toString().take(6)}@test.com"
        val proUid = signInTestUser(proEmail)
        val proRepo = FirestoreProxiRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            proRepo.ensureUserAccount(checkNotNull(auth.currentUser), "pro").getOrThrow()
        }

        val profileId = "pro_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val profile = ProfessionalProfile(
            id = profileId,
            userId = proUid,
            ownerName = "Koffi Mensah",
            businessName = "Atelier K-Tech",
            description = "Dépannage et installation électrique domestique et industrielle.",
            countryCode = "BJ",
            countryName = "Bénin",
            department = "Littoral",
            city = "Cotonou",
            neighborhood = "Cadjehoun",
            interventionZones = "Cotonou, Abomey-Calavi",
            phonePublic = "+22997001122",
            whatsappNumber = "+22997001122",
            primaryCategoryId = "cat_electricity",
            consentGiven = true
        )
        val services = listOf(
            ServiceItem(
                title = "Installation tableau électrique",
                description = "Pose complète aux normes",
                indicativePrice = 25000.0,
                pricingType = "STARTING_AT"
            )
        )
        val photos = listOf(
            PortfolioPhoto(
                imageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRg==",
                caption = "Tableau électrique installé à Cadjehoun"
            )
        )

        val submitResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            proRepo.submitProfile(profile, services, photos)
        }
        assertTrue("submitProfile failed: ${submitResult.exceptionOrNull()}", submitResult.isSuccess)

        // Pro sees own PENDING profile
        val myProfile = withTimeout(FLOW_TIMEOUT_MS) {
            proRepo.observeMyProfile().first { it != null && it.id == profileId }
        }
        assertEquals("PENDING", myProfile?.verificationStatus)

        // Client signs in: PENDING profile is NOT visible in approved search
        val clientUid = signInTestUser("client_${UUID.randomUUID().toString().take(6)}@test.com")
        val clientRepo = FirestoreProxiRepository(firestore, auth)
        val clientEnsure = withTimeout(DEFAULT_TIMEOUT_MS) {
            clientRepo.ensureUserAccount(checkNotNull(auth.currentUser), "client")
        }
        assertTrue("client ensureUserAccount failed: ${clientEnsure.exceptionOrNull()}", clientEnsure.isSuccess)

        val approvedBefore = withTimeout(FLOW_TIMEOUT_MS) {
            clientRepo.observeApprovedProfiles().first()
        }
        assertFalse(approvedBefore.any { it.id == profileId })

        // Verified Admin signs in and approves profile + writes audit log
        signInTestUser(FirestoreProxiRepository.BOOTSTRAPPED_ADMIN_EMAIL, verifyEmail = true)
        val adminRepo = FirestoreProxiRepository(firestore, auth)
        val adminUser = checkNotNull(auth.currentUser)
        val adminEnsure = withTimeout(DEFAULT_TIMEOUT_MS) {
            adminRepo.ensureUserAccount(adminUser, "admin")
        }
        assertTrue("admin ensureUserAccount failed: ${adminEnsure.exceptionOrNull()}", adminEnsure.isSuccess)
        assertEquals("admin", adminEnsure.getOrNull()?.role)

        val approveRes = withTimeout(DEFAULT_TIMEOUT_MS) {
            adminRepo.approveProfile(profileId)
        }
        assertTrue("approveProfile failed: ${approveRes.exceptionOrNull()}", approveRes.isSuccess)

        val logs = withTimeout(FLOW_TIMEOUT_MS) {
            adminRepo.observeAuditLogsForAdmin().first { list ->
                list.any { it.action == "APPROVE_PROFILE" && it.targetId == profileId }
            }
        }
        assertTrue(logs.any { it.targetId == profileId })

        // Client now discovers the APPROVED profile via search & increments view count & submits report
        signInTestUser("client2_${clientUid.take(5)}@test.com")
        val approvedSearch = withTimeout(FLOW_TIMEOUT_MS) {
            clientRepo.searchApprovedProfiles(
                queryText = "Atelier",
                countryCode = "BJ",
                department = "Littoral",
                city = "Cotonou",
                neighborhood = "Cadjehoun",
                categoryId = "cat_electricity"
            ).first { list -> list.any { it.id == profileId } }
        }
        assertTrue(approvedSearch.any { it.id == profileId })

        val viewResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            clientRepo.incrementProfileCounter(profileId, "viewsCount")
        }
        assertTrue("incrementProfileCounter failed: ${viewResult.exceptionOrNull()}", viewResult.isSuccess)

        val reportResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            clientRepo.submitReport(profileId, "Test signalement", "Vérification de signalement", "Client Test")
        }
        assertTrue("submitReport failed: ${reportResult.exceptionOrNull()}", reportResult.isSuccess)
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 8000L
        const val FLOW_TIMEOUT_MS = 5000L
    }
}

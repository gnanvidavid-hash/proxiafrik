const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const CLIENT_UID = "client_uid_01";
const PRO_UID = "pro_uid_01";
const ADMIN_UID = "admin_uid_01";
const ADMIN_EMAIL = "gnanvidavid@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function ts() {
  return new Date(Date.now() - 1000);
}

function validProPayload(userId, overrides = {}) {
  return {
    userId,
    ownerName: "Jean-Paul Mensah",
    businessName: "Plomberie Express Cotonou",
    slug: "plomberie-express-cotonou",
    description: "Artisan plombier qualifié pour dépannages rapides et installations sanitaires.",
    countryCode: "BJ",
    countryName: "Bénin",
    department: "Littoral",
    city: "Cotonou",
    neighborhood: "Cadjehoun",
    interventionZones: "Cotonou, Abomey-Calavi",
    phonePublic: "+22997112233",
    whatsappNumber: "+22997112233",
    profileImage: "",
    primaryCategoryId: "cat_plumbing",
    verificationStatus: "PENDING",
    publicationStatus: "PUBLISHED",
    openingHours: "Lun - Sam : 08h00 - 18h30",
    consentGiven: true,
    viewsCount: 0,
    whatsappClicksCount: 0,
    phoneClicksCount: 0,
    isSponsored: false,
    createdAt: ts(),
    updatedAt: ts(),
    ...overrides,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// 1. Unauthenticated rejections
test("Unauthenticated user: denied all reads and writes", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("professionals").where("verificationStatus", "==", "APPROVED").get());
  await assertFails(unauthDb.collection("categories").get());
  await assertFails(unauthDb.collection("users").doc(CLIENT_UID).get());
  await assertFails(unauthDb.collection("reports").get());
  await assertFails(unauthDb.collection("audit_logs").get());
});

// 2. Users RBAC & PII Isolation
test("User registration: allows client/pro roles, blocks self-assigned admin and cross-user reads", async () => {
  const clientDb = testEnv.authenticatedContext(CLIENT_UID, { email: "client@example.com", email_verified: true }).firestore();
  const proDb = testEnv.authenticatedContext(PRO_UID, { email: "pro@example.com", email_verified: true }).firestore();

  // Valid create as client
  await assertSucceeds(
    clientDb.collection("users").doc(CLIENT_UID).set({
      userId: CLIENT_UID,
      fullName: "Aïcha Dossou",
      email: "client@example.com",
      phone: "+22997000001",
      role: "client",
      accountStatus: "ACTIVE",
      createdAt: ts(),
      updatedAt: ts(),
    })
  );

  // Blocked: cannot self-register as admin
  await assertFails(
    proDb.collection("users").doc(PRO_UID).set({
      userId: PRO_UID,
      fullName: "Evil User",
      email: "evil@example.com",
      phone: "+22997000002",
      role: "admin",
      accountStatus: "ACTIVE",
      createdAt: ts(),
      updatedAt: ts(),
    })
  );

  // Blocked: cannot elevate own role to admin on update
  await assertFails(
    clientDb.collection("users").doc(CLIENT_UID).update({
      role: "admin",
      updatedAt: ts(),
    })
  );

  // Allowed: client switches own role from client to pro
  await assertSucceeds(
    clientDb.collection("users").doc(CLIENT_UID).update({
      role: "pro",
      updatedAt: ts(),
    })
  );

  // Blocked: cross-user PII read and unfiltered user list
  await assertFails(proDb.collection("users").doc(CLIENT_UID).get());
  await assertFails(proDb.collection("users").get());
});

// 3. Admin Email Spoofing Protection & Admin Powers
test("Admin access: rejects unverified admin email spoof, allows verified admin email", async () => {
  const spoofDb = testEnv.authenticatedContext("spoof_uid", {
    email: ADMIN_EMAIL,
    email_verified: false,
  }).firestore();

  await assertFails(
    spoofDb.collection("categories").doc("cat_plumbing").set({
      name: "Plomberie",
      slug: "plomberie",
      description: "Dépannage",
      iconKey: "plumbing",
      isActive: true,
      sortOrder: 1,
      createdAt: ts(),
      updatedAt: ts(),
    })
  );
  await assertFails(spoofDb.collection("reports").get());

  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: ADMIN_EMAIL,
    email_verified: true,
  }).firestore();

  await assertSucceeds(
    adminDb.collection("categories").doc("cat_plumbing").set({
      name: "Plomberie",
      slug: "plomberie",
      description: "Dépannage",
      iconKey: "plumbing",
      isActive: true,
      sortOrder: 1,
      createdAt: ts(),
      updatedAt: ts(),
    })
  );
  await assertSucceeds(adminDb.collection("reports").get());
});

// 4. Professional Profiles: Creation as PENDING, No Self-Approval, Approved-Only Public Queries
test("Professionals: enforces PENDING on create, rejects self-approval, isolates pending from public search", async () => {
  const proDb = testEnv.authenticatedContext(PRO_UID).firestore();
  const clientDb = testEnv.authenticatedContext(CLIENT_UID).firestore();
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, { email: ADMIN_EMAIL, email_verified: true }).firestore();

  // Blocked: Pro cannot create profile already APPROVED
  await assertFails(
    proDb.collection("professionals").doc("pro_bad").set(
      validProPayload(PRO_UID, { verificationStatus: "APPROVED" })
    )
  );

  // Blocked: Shadow update / Ghost field
  await assertFails(
    proDb.collection("professionals").doc("pro_shadow").set(
      validProPayload(PRO_UID, { isVerifiedByHand: true })
    )
  );

  // Allowed: Pro creates PENDING profile
  await assertSucceeds(
    proDb.collection("professionals").doc("pro_1").set(validProPayload(PRO_UID))
  );

  // Blocked: Pro cannot self-approve on update
  await assertFails(
    proDb.collection("professionals").doc("pro_1").update({
      verificationStatus: "APPROVED",
      updatedAt: ts(),
    })
  );

  // Blocked: Client cannot read PENDING profile or run unfiltered query
  await assertFails(clientDb.collection("professionals").doc("pro_1").get());
  await assertFails(clientDb.collection("professionals").get());
  await assertFails(clientDb.collection("professionals").where("verificationStatus", "==", "PENDING").get());

  // Allowed: Pro owner can query own PENDING profile
  await assertSucceeds(
    proDb.collection("professionals").where("userId", "==", PRO_UID).get()
  );

  // Allowed: Admin approves profile
  await assertSucceeds(
    adminDb.collection("professionals").doc("pro_1").update({
      verificationStatus: "APPROVED",
      updatedAt: ts(),
    })
  );

  // Allowed: Client queries approved & published profiles
  await assertSucceeds(
    clientDb.collection("professionals")
      .where("verificationStatus", "==", "APPROVED")
      .where("publicationStatus", "==", "PUBLISHED")
      .get()
  );

  // Allowed: Client increments viewsCount by +1 only
  await assertSucceeds(
    clientDb.collection("professionals").doc("pro_1").update({
      viewsCount: 1,
    })
  );

  // Blocked: Client cannot jump viewsCount by +5
  await assertFails(
    clientDb.collection("professionals").doc("pro_1").update({
      viewsCount: 5,
    })
  );
});

// 5. Subcollections & Reports
test("Subcollections and Reports: parent gate and admin-only moderation", async () => {
  const proDb = testEnv.authenticatedContext(PRO_UID).firestore();
  const clientDb = testEnv.authenticatedContext(CLIENT_UID).firestore();
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, { email: ADMIN_EMAIL, email_verified: true }).firestore();

  await proDb.collection("professionals").doc("pro_1").set(validProPayload(PRO_UID));

  // Pro adds own service and photo
  await assertSucceeds(
    proDb.collection("professionals").doc("pro_1").collection("services").doc("srv_1").set({
      professionalId: "pro_1",
      ownerUserId: PRO_UID,
      categoryId: "cat_plumbing",
      title: "Débouchage express",
      description: "Intervention rapide",
      indicativePrice: 15000,
      currencyCode: "FCFA",
      pricingType: "STARTING_AT",
      isActive: true,
      createdAt: ts(),
      updatedAt: ts(),
    })
  );

  // Blocked: Client cannot add service to Pro's profile
  await assertFails(
    clientDb.collection("professionals").doc("pro_1").collection("services").doc("srv_hack").set({
      professionalId: "pro_1",
      ownerUserId: CLIENT_UID,
      categoryId: "cat_plumbing",
      title: "Fake",
      description: "Hack",
      indicativePrice: 0,
      currencyCode: "FCFA",
      pricingType: "SUR_DEVIS",
      isActive: true,
      createdAt: ts(),
      updatedAt: ts(),
    })
  );

  // Client submits report on pro_1
  await assertSucceeds(
    clientDb.collection("reports").doc("rep_1").set({
      reporterUserId: CLIENT_UID,
      reporterName: "Aïcha",
      reportedProfileId: "pro_1",
      reason: "Numéro injoignable",
      description: "Sonne sans réponse.",
      status: "PENDING",
      createdAt: ts(),
      updatedAt: ts(),
    })
  );

  // Blocked: Pro/Client cannot resolve reports or list all reports
  await assertFails(proDb.collection("reports").get());
  await assertFails(clientDb.collection("reports").doc("rep_1").update({
    status: "RESOLVED",
    updatedAt: ts(),
  }));

  // Allowed: Admin resolves report and writes audit log
  await assertSucceeds(
    adminDb.collection("reports").doc("rep_1").update({
      status: "RESOLVED",
      reviewedBy: ADMIN_UID,
      reviewedAt: ts(),
      updatedAt: ts(),
    })
  );
  await assertSucceeds(
    adminDb.collection("audit_logs").doc("log_1").set({
      adminUserId: ADMIN_UID,
      action: "RESOLVE_REPORT",
      targetType: "REPORT",
      targetId: "rep_1",
      details: "Signalement traité",
      createdAt: ts(),
    })
  );
  await assertFails(clientDb.collection("audit_logs").get());
});

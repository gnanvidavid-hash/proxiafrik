# ProxiAfrik — GitHub Integration, Automated APK Builds & Releases Guide

This guide explains how to connect **ProxiAfrik** to GitHub, configure secure signing secrets, run Debug APK builds, and publish signed Production Release APKs via GitHub Releases.

---

## 1. Connect the Project to Your GitHub Repository

1. **Create a new repository on GitHub** (for example, `proxiafrik-android`). Do **not** initialize it with a conflicting `.gitignore` if you already have the project files.
2. **Initialize Git locally** (from the project root directory) and push to `main`:
   ```bash
   git init
   git branch -M main
   git add .
   git commit -m "Initial commit: ProxiAfrik Android app with CI/CD workflows"
   git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/<YOUR_REPO_NAME>.git
   git push -u origin main
   ```
3. Verify `.gitignore` excludes sensitive files before pushing:
   - Never commit `*.jks`, `*.keystore` (release keys), `keystore.properties`, `.env`, or `local.properties`.
   - `git status` should **never** list your production `.jks` keystore.

---

## 2. Generate a Production Keystore & Configure GitHub Secrets

`app/build.gradle.kts` is configured to read release signing credentials from environment variables (`KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`) with key alias **`upload`**.

### Step A — Generate your production upload keystore locally (one-time)
Run this command on your local computer (**not** inside a committed project directory, e.g., in `~/keystores/`):

```bash
mkdir -p ~/keystores
keytool -genkeypair -v \
  -keystore ~/keystores/proxiafrik-release.jks \
  -alias upload \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```
- Enter a strong **keystore password** (`STORE_PASSWORD`) and **key password** (`KEY_PASSWORD`).
- Keep `proxiafrik-release.jks` backed up safely offline (e.g., encrypted drive / password manager). **Never commit this file to GitHub.**

### Step B — Base64-encode the keystore
- **Linux / macOS:**
  ```bash
  base64 -w 0 ~/keystores/proxiafrik-release.jks
  ```
  *(On macOS, omit `-w 0`: `base64 -i ~/keystores/proxiafrik-release.jks | tr -d '\n'`)*
- **Windows (PowerShell):**
  ```powershell
  [Convert]::ToBase64String([IO.File]::ReadAllBytes("$HOME\keystores\proxiafrik-release.jks"))
  ```

### Step C — Add GitHub Repository Secrets
In your GitHub repository, open:
**Settings → Secrets and variables → Actions → New repository secret**

Add the following **3 secrets**:

| Secret Name | Value |
|---|---|
| `KEYSTORE_BASE64` | The single-line Base64 string of `proxiafrik-release.jks` |
| `STORE_PASSWORD` | Your keystore password |
| `KEY_PASSWORD` | Your `upload` key password |

*(Optional)* If you use Gemini/third-party API keys via `.env` / Secrets Gradle Plugin, add them as Secrets only if required by your production configuration.

---

## 3. Run a Debug APK Build (`.github/workflows/publish-apk.yml`)

The Debug workflow runs automatically and can also be triggered manually:

- **Automatic triggers:**
  - Every `push` to the `main` branch
  - Every `pull_request` targeting `main`
- **Manual trigger (`workflow_dispatch`):**
  1. Go to your GitHub repository → **Actions** tab.
  2. Select **Build Debug APK** in the left sidebar.
  3. Click **Run workflow** → choose branch `main` → click **Run workflow**.

---

## 4. Download the Debug APK from GitHub Actions Artifacts

1. Open the **Actions** tab in your GitHub repository.
2. Click the completed **Build Debug APK** workflow run (green checkmark).
3. Scroll down to the **Artifacts** section at the bottom of the summary page.
4. Click **`PROXIAFRIK-debug`** to download the ZIP archive.
5. Unzip it to extract **`PROXIAFRIK-debug.apk`** and install it on your Android phone for testing.

---

## 5. Publish a Version Tag for a Production Release (`.github/workflows/release-apk.yml`)

Whenever you are ready to release a signed production version (e.g., `v1.0.0`):

```bash
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Release ProxiAfrik v1.0.0"
git push origin v1.0.0
```

This automatically triggers **Production Release APK** (`.github/workflows/release-apk.yml`), which:
1. Validates that `KEYSTORE_BASE64`, `STORE_PASSWORD`, and `KEY_PASSWORD` exist.
2. Runs all unit and Robolectric tests (`:app:testDebugUnitTest`).
3. Decodes your keystore temporarily inside the runner, builds the signed Release APK (`:app:assembleRelease`), and renames it to **`PROXIAFRIK-v1.0.0.apk`**.
4. Verifies the cryptographic signature with `apksigner verify --verbose --print-certs` and confirms it is not a debug certificate.
5. Deletes the temporary keystore from the runner (`if: always()`).
6. Creates a GitHub Release tagged **`v1.0.0`** with release notes, **`PROXIAFRIK-v1.0.0.apk`**, and **`PROXIAFRIK-v1.0.0.apk.sha256`**.

*(You can also trigger a manual release from **Actions → Production Release APK → Run workflow** by entering the `version_tag` input, e.g., `v1.0.0`.)*

---

## 6. Download the Production APK from GitHub Releases

1. Go to your GitHub repository's main page.
2. Click **Releases** on the right sidebar (or visit `https://github.com/<YOUR_USERNAME>/<YOUR_REPO>/releases`).
3. Open the release (e.g., **ProxiAfrik v1.0.0**).
4. Under **Assets**, download **`PROXIAFRIK-v1.0.0.apk`** directly onto your Android phone or computer.
5. *(Optional)* Verify file integrity using `PROXIAFRIK-v1.0.0.apk.sha256`:
   ```bash
   sha256sum -c PROXIAFRIK-v1.0.0.apk.sha256
   ```

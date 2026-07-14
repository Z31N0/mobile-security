# Documentation — Member 1 (Moestafa) — **catsadoption_shop**

**Role:** UI & API Security Layer (Moestafa)

**Scope covered:** Commit 1 (UI + live API), Commit 2 (login + dashboard), Commit 3 (secure API client with SSL pinning).

---

## Project Overview

The *catsadoption_shop* application is designed as a simple Android project built with **Kotlin**, **Jetpack Compose**, and **Retrofit/OkHttp**. The goal of this module is to provide an interactive user interface that communicates with both secure and insecure endpoints for testing in a mobile pentesting environment.

Member 1 (Moestafa) focused on all UI-related components and the creation of the secure communication layer. The implemented work establishes the foundation of the application with working screens and secure API integration ready for use by other team members.

---

## Summary

Three main commits were developed:

1. **Commit 1 – Base UI and API integration:** creation of the main screen displaying live data (cat images) from a public API.
2. **Commit 2 – Login and dashboard:** addition of navigation, user login simulation, and dashboard screen with logout functionality.
3. **Commit 3 – Secure communication setup:** implementation of a secure Retrofit/OkHttp client with SSL certificate pinning to protect communication integrity.

The secure client is operational and can be integrated by other team members into their own modules when performing secure API requests.

---

## Deliverables and Purpose

### Commit 1 — Base app & Real Listings

**Objective:** Build a clean Compose UI that fetches and displays real cat images using The Cat API.

**Main Files:**
- `MainActivity.kt` – Entry point; sets up navigation and Compose structure.
- `CatListingsScreen.kt` – Displays a list of cats using `LazyColumn` and Coil for image loading.
- `CatApiService.kt` & `CatRepository.kt` – Implement the Retrofit client to perform an HTTP request to `https://api.thecatapi.com/v1/images/search?limit=10`.

**Result:**
- Functional overview screen with network connection and live data.
- One external API request established (non-secure public endpoint).

---

### Commit 2 — Login & Dashboard

**Objective:** Provide navigation and a second UI screen to simulate user accounts.

**Main Files:**
- `LoginScreen.kt` – Simple username field and button that navigates to the dashboard.
- `UserDashboardScreen.kt` – Displays the logged-in username and placeholder data for adopted cats. Includes **Logout** button to return to login.

**Flow:**
- Screen 1 (Listings) → Screen 2 (Login) → Screen 3 (Dashboard/{username}).
- Dashboard temporarily reuses data from `CatRepository` to populate a list.

**Result:**
- Functional app navigation with 2+ screens.
- Establishes clear separation between UI components and data logic.

---

### Commit 3 — Secure API Client (SSL Pinning)

**Objective:** Implement a secure API client with SSL pinning to prevent interception or tampering.

**Main Files:**
- `SecureApiService.kt` – Defines secure endpoint and data model (`SecureCatData`).
- `SecureApiClient.kt` – Implements Retrofit + OkHttp client with `CertificatePinner` enforcing SHA-256 pin validation.

**Configured Host:**
- `api.npoint.io` – free JSON hosting service used to simulate a secure API endpoint.
- Example Base URL: `https://api.npoint.io/b90955cbda6a6c5bc66e/`
- Pinned Key: `sha256/0Xa7UdRqkzcnNrkuBuWltp9rt1S7FS3KZ7z4JQ8YdRU=`

**Behavior:**
- Any certificate mismatch or proxy interception (e.g., via BurpSuite) triggers `SSLHandshakeException`.
- No caching or fallback is implemented to ensure strict behavior.

---

## Project File Structure

```
app/src/main/java/com/example/catsadoption_shop/
  MainActivity.kt
  /ui/screens/
      CatListingsScreen.kt
      LoginScreen.kt
      UserDashboardScreen.kt
  /data/
      CatApiService.kt
      CatRepository.kt
  /data/secure/
      SecureApiService.kt
      SecureApiClient.kt
  /ui/theme/
      CatsAdoptionShopTheme.kt
```

---

## Technical Explanation – SSL Pinning

SSL pinning is implemented in `SecureApiClient.kt` using the following process:

1. Extract the server’s public key fingerprint (SHA-256) using OpenSSL.
2. Embed this pin value directly in the client code.
3. Use OkHttp’s `CertificatePinner` to compare the pinned key against the certificate received during HTTPS handshake.

If the keys do not match, the connection is immediately terminated, protecting the app from MITM attacks.

**OpenSSL Command to Compute Pin:**
```bash
echo | openssl s_client -connect api.npoint.io:443 -servername api.npoint.io 2>/dev/null \
  | openssl x509 -pubkey -noout \
  | openssl pkey -pubin -outform der \
  | openssl dgst -sha256 -binary \
  | openssl enc -base64 \
  | awk '{print "sha256/" $0}'
```

---

## Testing and Validation

To verify Commit 1–2:
1. Run the application on an emulator or real device.
2. Verify that The Cat API images load correctly.
3. Navigate through Login → Dashboard → Logout.

To verify Commit 3:
- Call `SecureApiClient.api.getSecureCat()` from any coroutine and monitor logs.
- Without interception, the request succeeds and returns the secure JSON object.
- When intercepted or modified, the SSL handshake fails, showing the protection in action.

---

**End of documentation — Member 1 (Moestafa)**


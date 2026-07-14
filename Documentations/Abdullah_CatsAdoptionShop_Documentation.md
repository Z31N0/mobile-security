# Documentation — Member 2 (Abdullah) — **catsadoption_shop**

**Role:** Database & Local Storage Security (Abdullah)

**Scope covered:** Implementation of local storage, user authentication and analysis of local data security vulnerabilities (Weak Encryption & Frida Bypass)

---

## Project Overview

This module focuses on the implementation of a local data persistence and authentication layer for this application using **Room Databases**, a local database was made to manage user accounts. For the sake of demonstration purposes, we intentionally created 2 vulnerabilities: weak credential storage (encryption using base64) and a bypassable client-side security check.

Member 2 (Abdullah) was resposible for setting up the database, creating the login/register logic, and documentation concerning the security flaws and the exploitation methods.

---

## Summary

The development here can be broken down into three main parts:

1. **Room Database & Authentication:** Creation of a functional **SQLi-safe Room database**, used to handle user registration and login.
2. **Intentional Vulnerabilities:** Two security flaws were intentionally implemented:
    - **Weak Encryption:** User passwords are "encrypted" using base64, something very simply reversable.
    - **Frida Bypass Point:** A client-side root detection check was added which blocks logins from rooted devices, made to be bypassable.
3. **Security Testing & Exploitation:** The forementioned vulnerabilities were tested and successfully exploited.

---

## Deliverables and Purpose

### Deliverable 1 - Room Database & Local Authentication

**Objective:** Build a clean, SQLi-safe database for user management.

**Main Files:**

- `User.kt` - The Room `@Entity` defining the **users** table schema (`id, username, encryptedPassword`).
- `UserDao.kt` - The **Data Access Object** `interface` with `@Query` and `@Insert` methods. Room uses this to generate safe, parameterized queries.
- `AppDatabase.kt` - The main `RoomDatabase` class the provides a single, app-wide database instance and exposes DAOs.
- `LoginScreen.kt` - UI updated to include both "Register" and "Login" options, interacting with the database via a repository.

**Result:**

- A fully functional local registration and login system.
- Data persistence that is inherently protected from SQLi.

---

### Deliverable 2 - Intentional Security Vulnerabilities

**Objective:** Implement 2 common mobilie security flaws for demonstrations and exploitations.

**Main Files:**

1. **Weak Encryption:**
    - `WeakEncryption.kt`: A utility object that uses `android.util.Base64` to encode and decode passwords.
2. **Frida Bypass Point:**
    - `RootDetector.kt`: A simple utility that checks for the existence of common `su` binaries.
    - `UserRepository.kt`: A repository class that centralizes data logic. It contains a private `isAccessSecure()` function that uses the `RootDetector` to decide wheter the runtime environment is trusted or not.

**Behavior:**

- User passwords are not truly encrypted, only encoded, and stored in the local database file.
- The `LoginScreen` will fail to log into a user on a rooted device because `isAccessSecure()` returns `false`.

---

## Technical Explanation - Frida Bypass

The `isAccessSecure()` function in `UserRepository.kt` is the designated bypass point. The exploitation process involves using Frida to hook this function at runtime and alter its behavior.

1. **Target Identification:** The function `com.example.catsadoption_shop.data.UserRepository.isAccessSecure()` is identified as the target.
2. **Runtime Hooking:** A Frida script (bypass-project,js) is written to find this method in the app's memory once it's launched.
3. **Implementation Replacement:** The original loggic of the `isAccessSecure()` function is replaced entirely by the script's logic, which just returns `true`, neutralizing the root check.

**Frida Bypass Script (bypass-project.js):**
```js
// Frida script to bypass the root check in the CatsAdoption-Shop app

console.log(
  "[*] Frida script loaded. Waiting for the app to call the target function..."
);

// Use setTimeout to ensure Java classes are loaded before we try to hook them
setTimeout(function () {
  // Java.perform is the main entry point for any Frida script interacting with Java code
  Java.perform(function () {
    console.log(
      "[*] Inside Java.perform(). Searching for the UserRepository class..."
    );

    // 1. Get a handle to the UserRepository class
    const UserRepository = Java.use(
      "com.example.catsadoption_shop.data.UserRepository"
    );

    // 2. Hook the target method: isAccessSecure()
    UserRepository.isAccessSecure.implementation = function () {
      // Log a message to the Frida console to confirm our hook is working
      console.log("[+] Intercepted call to isAccessSecure()!");
      console.log(
        "[+]   Original function would have returned 'false' on a rooted device."
      );
      console.log("[+]   Forcing it to return 'true' instead.");

      // 3. Force the function to return 'true'
      // This bypasses the root check completely.
      return true;
    };

    console.log("[*] UserRepository.isAccessSecure() is now hooked.");
  });
}, 0);
```
---

## Testing and Validation

To verify the database and test the vulnerabilities:

1. **Verify Database Works:**
    - Use **Android Studio > View > Tool Window > App Inspection**.
    - Register a new user in the app.
    - **Result:** Find the new registered user in the database.

2. **Verify SQLi Protection:**
    - On the login screen, enter a classic SQLi payload like `admin' OR '1'='1 -- ` in the username field.
    - **Result:** Login fails. This proves Room treated the input as data, not as a command.

3. **Verify Frida Bypass:**
    - Launch a rooted AVD and run `frida-server` on it.
    - Launch the app using the Frida script using the following cmd:
    ```
    frida -U -f com.example.catsadoption_shop -l bypass-project.js
    ```
    - Attempt to log in on the rooted device.
    - **Result:** The login succeeds (normally it should've failed because of the `RootDetector`).

![alt text](images/image.png)
package com.example.catsadoption_shop.data

import com.example.catsadoption_shop.security.RootDetector

class UserRepository(private val userDao: UserDao) {
    /**
     * This is the FRIDA BYPASS POINT.
     * A Frida script can hook this method to always return 'true',
     * thus bypassing the root check.
     */
    private fun isAccessSecure(): Boolean {
        if (RootDetector.isDeviceRooted()) {
            println("Device is rooted! Denying data access.")
            return false // Denies access on rooted devices
        }
        return true // Grants access
    }

    suspend fun getUserByUsername(username: String): User? {
        // Only perform the check if security check passes
        if (!isAccessSecure()) {
            return null
        }
        return userDao.getUserByUsername(username)
    }

    // Registration doesn't need a security check
    suspend fun registerUser(user: User) {
        userDao.registerUser(user)
    }
}
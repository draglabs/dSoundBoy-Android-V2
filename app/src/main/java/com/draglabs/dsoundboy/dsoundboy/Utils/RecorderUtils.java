package com.draglabs.dsoundboy.dsoundboy.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.draglabs.dsoundboy.dsoundboy.Models.RecorderModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * <p>Created by davrukin on 8/23/17.</p>
 * <p>Performs functions pertaining to the recorder</p>
 */

@SuppressWarnings("DefaultFileTemplate")
public class RecorderUtils {

    private Context context;
    private Activity activity;
    private RecorderModel recorderModel;
    private Thread recordingThread;

    /**
     * Constructor for the RecorderUtils
     * @param context the app's context
     * @param bandData the band's data
     * @param activity the activity calling this constructor
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RecorderUtils(Context context, String[] bandData, Activity activity) {
        this.recorderModel = new RecorderModel();

        this.context = context;
        this.activity = activity;
        this.recordingThread = new Thread();

        recorderModel.setBandData(bandData);
        recorderModel.setAudioSavePathInDevice(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings" + recorderModel.getPathname());
        recorderModel.setPathname(createAudioPathname(bandData, recorderModel.getEXTENSION()));
        recorderModel.setAudioRecord(new AudioRecord(recorderModel.getRecorderAudioSource(),
                                                        recorderModel.getRecordingSampleRate(),
                                                        recorderModel.getRecordingChannels(),
                                                        recorderModel.getRecordingAudioEncoding(),
        recorderModel.getBufferElementsToRec() * recorderModel.getBytesPerElement()));
    }

    /**
     * Starts recdording
     * @param activity the activity calling this method
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startRecording(Activity activity) {
        if (checkPermissions()) {
            if (recorderModel.getMediaRecorder() != null) {
                recorderModel.setPathname(createAudioPathname(
                        PrefUtils.getArtistName(activity),
                        PrefUtils.getRecordingDescription(activity),
                        PrefUtils.getRecordingVenue(activity),
                        PrefUtils.getArtistEmail(activity),
                        new Date()) + recorderModel.getEXTENSION());

                recorderModel.setAudioSavePathInDevice(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings/" + recorderModel.getPathname());

                recorderModel.setStartTime(new Date()); // TODO: FILE NAMES NOT BEING SET PROPERLY
                String testString = "startTime.toString(): " + recorderModel.getStartTime().toString() +
                        ", startTime.getTime(): " + recorderModel.getStartTime().getTime();
                Toast.makeText(context, testString, Toast.LENGTH_LONG).show();
                System.out.println(testString);

                try {
                    File recording = new File(recorderModel.getAudioSavePathInDevice());
                    if (!recording.getParentFile().exists()) {
                        recording.getParentFile().mkdirs(); // result ignored
                    }
                    if (!recording.exists()) {
                        recording.createNewFile(); // result ignored
                    }

                    recorderModel.setRecording(true);
                    recordingThread = new Thread(() -> writeAudioDataToFile(recorderModel.getAudioSavePathInDevice()), "AudioRecorder Thread");
                    recordingThread.start();

                    Toast.makeText(context, "Recording Started.", Toast.LENGTH_LONG).show();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Recording Error. IllegalArgumentException.", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Recording Error. IOException.", Toast.LENGTH_LONG).show();
                }
            } else {
                recorderModel.setMediaRecorder(recorderModel.mediaRecorderReady());
                startRecording(activity);
            }
        } else {
            requestPermission();
        }
    }

    /**
     * Stops recording
     */
    public void stopRecording() {
        // TODO: when "Submit" is clicked, finalize recording with this method
        recorderModel.setEndTime(new Date());

        try {
            if (recorderModel.getAudioRecord() != null) {
                recorderModel.setRecording(false);
                recorderModel.getAudioRecord().stop();
                recorderModel.getAudioRecord().release();
                recorderModel.setAudioRecord(null);
                recordingThread = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, "Recording Completed.", Toast.LENGTH_LONG).show();
    }

    /**
     * Resets the recording
     */
    public void resetRecording() {
        recorderModel.getMediaRecorder().reset();
    }

    /**
     * Converts an array of the short data type to an array of bytes, used for the recorder
     * @param shorts an array of the short data type
     * @return an array of bytes
     */
    private byte[] shortToByte(short[] shorts) {
        int shortArraySize = shorts.length;
        byte[] bytes = new byte[shortArraySize * 2];
        for (int i = 0; i < shortArraySize; i++) {
            bytes[i * 2] = (byte)(shorts[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte)(shorts[i] >> 8);
            shorts[i] = 0;
        }
        return bytes;
    }

    /**
     * Write the audio data to the file
     * @param pathname the local path to the file
     */
    private void writeAudioDataToFile(String pathname) {
        short[] shorts = new short[recorderModel.getBufferElementsToRec()];

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(pathname);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (recorderModel.isRecording()) {
            recorderModel.getAudioRecord().read(shorts, 0, recorderModel.getBufferElementsToRec());
            try {
                byte[] data = shortToByte(shorts);
                assert fileOutputStream != null;
                fileOutputStream.write(data, 0, recorderModel.getBufferElementsToRec() * recorderModel.getBytesPerElement());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            assert fileOutputStream != null;
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a random audio pathname
     * @param length the length of the name
     * @return the name
     */
    public String createRandomAudioPathname(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(recorderModel.getRANDOM_AUDIO_FILE_NAME().charAt(recorderModel.getRandom().nextInt(recorderModel.getRANDOM_AUDIO_FILE_NAME().length())));
        }
        return stringBuilder.toString();
    }

    /**
     * Creates a pathname based on an array of strings containing the band's data
     * @param data the band's data
     * @param extension the file extension
     * @return the new file's pathname
     */
    private String createAudioPathname(String[] data, String extension) {
        return createAudioPathname(data[0], data[1], data[2], data[3], new Date()) + extension;
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
    private String createAudioPathname(String bandName, String description, String venue, String email, Date date) {
        // format: BAND-NAME_GENRE-NAME_VENUE_EMAIL_DAY-MONTH-YEAR_HOUR:MINUTE:SECOND:MILLISECOND
        Locale currentLocale = Locale.getDefault();
        //String pattern = "dd-MM-yyyy_HH:mm:ss:SSS";
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, currentLocale);
        String dateString = simpleDateFormat.format(date);

        return (bandName + "_" + description + "_" + email + "_" + venue + "_" + dateString);
    }

    /**
     * Requests permission to write to external storage and to record audio
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, 1);
    }

    /**
     * Performs actions once permissions have been granted
     * @param requestCode the request code
     * @param permissions string array of the permissions
     * @param grantResults integer array of the permission results
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    boolean storagePermission = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    boolean recordPermission = (grantResults[1] == PackageManager.PERMISSION_GRANTED);

                    if (storagePermission && recordPermission) {
                        Toast.makeText(context, "Permission Granted.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Permission Denied.", Toast.LENGTH_LONG).show();
                    }
                }
            break;
        }
    }

    /**
     * Checks to see if the permissions have been accepted
     * @return true if accepted, false if not
     */
    private boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, RECORD_AUDIO);
        return ((result == PackageManager.PERMISSION_GRANTED) && (result1 == PackageManager.PERMISSION_GRANTED));
    }

    /**
     * Checks to see if the app can record audio
     * @return true if yes, false if no
     */
    private boolean checkAudioPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Gets the local path where the audio is saved
     * @return the local path where the audio is saved
     */
    public String getAudioSavePathInDevice() {
        return recorderModel.getAudioSavePathInDevice();
    }
}

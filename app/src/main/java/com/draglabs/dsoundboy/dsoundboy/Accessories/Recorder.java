package com.draglabs.dsoundboy.dsoundboy.Accessories;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.draglabs.dsoundboy.dsoundboy.Utils.PrefUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by davrukin on 8/23/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Recorder {

    private Context context;
    private Activity thatMainActivity;
    private RecorderSettings recorderSettings;
    private Thread recordingThread;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Recorder(Context context, String[] bandData, Activity thatMainActivity) {
        this.recorderSettings = new RecorderSettings();

        this.context = context;
        this.thatMainActivity = thatMainActivity;
        this.recordingThread = new Thread();

        recorderSettings.setBandData(bandData);
        recorderSettings.setAudioSavePathInDevice(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings" + recorderSettings.getPathname());
        recorderSettings.setPathname(createAudioPathname(bandData, recorderSettings.getEXTENSION()));
        recorderSettings.setAudioRecord(new AudioRecord(recorderSettings.getRecorderAudioSource(),
                                                        recorderSettings.getRecordingSampleRate(),
                                                        recorderSettings.getRecordingChannels(),
                                                        recorderSettings.getRecordingAudioEncoding(),
        recorderSettings.getBufferElementsToRec() * recorderSettings.getBytesPerElement()));
    }

    public void startStopRecording(int startStopClickCount, Chronometer chronometer, ImageView recordingImage) {
        if (checkPermissions()) {

            recorderSettings.setStartTime(new Date());
            String testString = "startTime.toString(): " + recorderSettings.getStartTime().toString() +
                                ", startTime.getTime(): " + recorderSettings.getStartTime().getTime();
            Toast.makeText(context, testString, Toast.LENGTH_LONG).show();
            System.out.println(testString);

            if (startStopClickCount % 2 != 0) {
                // TODO: start

                recorderSettings.setRecording(true);
                recordingThread = new Thread(() -> writeAudioDataToFile(recorderSettings.getAudioSavePathInDevice()), "AudioRecorder Thread");
                if (!recordingThread.isAlive()) {
                    recordingThread.start();
                } else {
                    recordingThread.notify();
                }
                Toast.makeText(context, "Started recording.", Toast.LENGTH_LONG).show();
                chronometer.start();
                recordingImage.setVisibility(View.VISIBLE);
            } else {
                // TODO: pause

                try {
                    recordingThread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(context, "Paused recording.", Toast.LENGTH_LONG).show();
                chronometer.stop();
                recordingImage.setVisibility(View.INVISIBLE);
            }
        } else {
            requestPermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startRecording(Activity activity) {
        if (checkPermissions()) {
            if (recorderSettings.getMediaRecorder() != null) {
                recorderSettings.setPathname(createAudioPathname(
                        PrefUtils.getArtistName(activity),
                        PrefUtils.getRecordingDescription(activity),
                        PrefUtils.getRecordingVenue(activity),
                        PrefUtils.getArtistEmail(activity),
                        new Date()) + recorderSettings.getEXTENSION());

                recorderSettings.setAudioSavePathInDevice(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings/" + recorderSettings.getPathname());

                recorderSettings.setStartTime(new Date()); // TODO: FILE NAMES NOT BEING SET PROPERLY
                String testString = "startTime.toString(): " + recorderSettings.getStartTime().toString() +
                        ", startTime.getTime(): " + recorderSettings.getStartTime().getTime();
                Toast.makeText(context, testString, Toast.LENGTH_LONG).show();
                System.out.println(testString);

                try {
                    File recording = new File(recorderSettings.getAudioSavePathInDevice());
                    if (!recording.getParentFile().exists()) {
                        recording.getParentFile().mkdirs();
                    }
                    if (!recording.exists()) {
                        recording.createNewFile();
                    }

                    recorderSettings.setRecording(true);
                    recordingThread = new Thread(() -> writeAudioDataToFile(recorderSettings.getAudioSavePathInDevice()), "AudioRecorder Thread");
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
                recorderSettings.setMediaRecorder(recorderSettings.mediaRecorderReady());
                startRecording(activity);
            }
        } else {
            requestPermission();
        }
    }

    public void stopRecording() {
        // TODO: when "Submit" is clicked, finalize recording with this method
        recorderSettings.setEndTime(new Date());

        try {
            if (recorderSettings.getAudioRecord() != null) {
                recorderSettings.setRecording(false);
                recorderSettings.getAudioRecord().stop();
                recorderSettings.getAudioRecord().release();
                recorderSettings.setAudioRecord(null);
                recordingThread = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, "Recording Completed.", Toast.LENGTH_LONG).show();
    }

    public void resetRecording() {
        recorderSettings.getMediaRecorder().reset();
    }

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

    private void writeAudioDataToFile(String pathname) {
        short[] shorts = new short[recorderSettings.getBufferElementsToRec()];

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(pathname);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (recorderSettings.isRecording()) {
            recorderSettings.getAudioRecord().read(shorts, 0, recorderSettings.getBufferElementsToRec());
            try {
                byte[] data = shortToByte(shorts);
                assert fileOutputStream != null;
                fileOutputStream.write(data, 0, recorderSettings.getBufferElementsToRec() * recorderSettings.getBytesPerElement());
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

    public String createRandomAudioPathname(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(recorderSettings.getRANDOM_AUDIO_FILE_NAME().charAt(recorderSettings.getRandom().nextInt(recorderSettings.getRANDOM_AUDIO_FILE_NAME().length())));
        }
        return stringBuilder.toString();
    }

    private String createAudioPathname(String[] data, String extension) {
        return createAudioPathname(data[0], data[1], data[2], data[3], new Date()) + extension;
    }

    private String createAudioPathname(String bandName, String description, String venue, String email, Date date) {
        // format: BAND-NAME_GENRE-NAME_VENUE_EMAIL_DAY-MONTH-YEAR_HOUR:MINUTE:SECOND:MILLISECOND
        Locale currentLocale = Locale.getDefault();
        //String pattern = "dd-MM-yyyy_HH:mm:ss:SSS";
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, currentLocale);
        String dateString = simpleDateFormat.format(date);

        return (bandName + "_" + description + "_" + email + "_" + venue + "_" + dateString);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(thatMainActivity, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, 1);
    }

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

    private boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, RECORD_AUDIO);
        return ((result == PackageManager.PERMISSION_GRANTED) && (result1 == PackageManager.PERMISSION_GRANTED));
    }

    public String getAudioSavePathInDevice() {
        return recorderSettings.getAudioSavePathInDevice();
    }
}

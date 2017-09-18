package com.draglabs.dsoundboy.dsoundboy.Acessories;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
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
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by davrukin on 8/23/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Recorder {

    private Context context;

    private Date startTime;
    private Date endTime;
    private long duration;

    private String audioSavePathInDevice;
    private MediaRecorder mediaRecorder;
    private Random random;
    private final String RANDOM_AUDIO_FILE_NAME = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int REQUEST_PERMISSION_CODE = 1;
    //private MediaPlayer mediaPlayer;

    private String[] bandData;

    private static final int RECORDER_AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int RECORDING_ENCODING_BIT_RATE = 24;
    private static final int RECORDER_SAMPLE_RATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_OUTPUT_FORMAT = MediaRecorder.OutputFormat.MPEG_4;
    @RequiresApi(LOLLIPOP) private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_FLOAT;
    private AudioRecord audioRecord;
    private Thread recordingThread;
    private boolean isRecording = false;

    private final int bufferElementsToRec = 1024;
    private final int bytesPerElement = 2;

    private final String EXTENSION = ".pcm";
    private String pathname;

    private Activity thatMainActivity;

    //private final String FACEBOOK_USER_ID = Profile.getCurrentProfile().getId();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Recorder(Context context, String[] bandData, Activity thatMainActivity) {
        this.context = context;
        this.bandData = bandData;
        this.thatMainActivity = thatMainActivity;

        mediaRecorder = new MediaRecorder();

        //audioRecord = new AudioRecord(RECORDER_AUDIO_SOURCE, RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferElementsToRec * bytesPerElement);

        pathname = createAudioPathname(bandData, EXTENSION); // TODO: how to generate recordingID? ask Shyam

        audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings" + pathname;

        try {
            File recording = new File(audioSavePathInDevice);
            if (!recording.getParentFile().exists()) {
                recording.getParentFile().mkdirs();
            }
            if (!recording.exists()) {
                recording.createNewFile();
            }
            audioRecord = new AudioRecord(RECORDER_AUDIO_SOURCE, RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferElementsToRec * bytesPerElement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startStopRecording(int startStopClickCount, Chronometer chronometer, ImageView recordingImage) {
        if (checkPermissions()) {

            startTime = new Date();
            String testString = "startTime.toString(): " + startTime.toString() + ", startTime.getTime(): " + startTime.getTime();
            Toast.makeText(context, testString, Toast.LENGTH_LONG).show();
            System.out.println(testString);

            if (startStopClickCount % 2 != 0) {
                // TODO: start

                isRecording = true;
                recordingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeAudioDataToFile(audioSavePathInDevice);
                    }
                }, "AudioRecorder Thread");
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
    public void startRecording() { // TODO: recording name is the Facebook user id
        if (checkPermissions()) {
            //String EXTENSION = ".pcm";
            pathname = createAudioPathname(bandData, EXTENSION);

            audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings/" + pathname;

            startTime = new Date();
            String testString = "startTime.toString(): " + startTime.toString() + ", startTime.getTime(): " + startTime.getTime();
            Toast.makeText(context, testString, Toast.LENGTH_LONG).show();
            System.out.println(testString);

            try {
                File recording = new File(audioSavePathInDevice);
                if (!recording.getParentFile().exists()) {
                    recording.getParentFile().mkdirs();
                }
                if (!recording.exists()) {
                    recording.createNewFile();
                }

                audioRecord = new AudioRecord(RECORDER_AUDIO_SOURCE, RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferElementsToRec * bytesPerElement);
                isRecording = true;
                recordingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeAudioDataToFile(audioSavePathInDevice);
                    }
                }, "AudioRecorder Thread");
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
            requestPermission();
        }
    }

    public void stopRecording() {
        // TODO: when "Submit" is clicked, finalize recording with this method
        endTime = new Date();

        try {
            if (audioRecord != null) {
                isRecording = false;
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
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
        mediaRecorder.reset();
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
        short[] shorts = new short[bufferElementsToRec];

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(pathname);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            audioRecord.read(shorts, 0, bufferElementsToRec);
            try {
                byte[] data = shortToByte(shorts);
                assert fileOutputStream != null;
                fileOutputStream.write(data, 0, bufferElementsToRec * bytesPerElement);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void mediaRecorderReady() {
        mediaRecorder.setAudioSource(RECORDER_AUDIO_SOURCE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mediaRecorder.setOutputFormat(RECORDER_OUTPUT_FORMAT);
            mediaRecorder.setAudioEncoder(RECORDER_AUDIO_ENCODING);
        }
        mediaRecorder.setAudioChannels(RECORDER_CHANNELS);
        mediaRecorder.setAudioEncodingBitRate(RECORDING_ENCODING_BIT_RATE);
        mediaRecorder.setAudioSamplingRate(RECORDER_SAMPLE_RATE);
        mediaRecorder.setOutputFile(audioSavePathInDevice);
    }

    public String createRandomAudioPathname(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(RANDOM_AUDIO_FILE_NAME.charAt(random.nextInt(RANDOM_AUDIO_FILE_NAME.length())));
        }
        return stringBuilder.toString();
    }

    private String createAudioPathname(String[] data, String extension) {
        return createAudioPathname(data[0], data[1], data[2], data[3], new Date()) + extension;
    }

    private String createAudioPathname(String bandName, String description, String venue, String email, Date date) {
        // format: BAND-NAME_GENRE-NAME_VENUE_EMAIL_DAY-MONTH-YEAR_HOUR:MINUTE:SECOND:MILLISECOND
        Locale currentLocale = Locale.getDefault();
        String pattern = "dd-MM-yyyy_HH:mm:ss:SSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, currentLocale);
        String dateString = simpleDateFormat.format(date);

        return (bandName + "_" + description + "_" + email + "_" + venue + "_" + dateString);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(thatMainActivity, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQUEST_PERMISSION_CODE:
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
}

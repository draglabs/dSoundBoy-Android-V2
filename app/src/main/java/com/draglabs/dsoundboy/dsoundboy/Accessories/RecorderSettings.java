package com.draglabs.dsoundboy.dsoundboy.Accessories;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Date;
import java.util.Random;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by davrukin on 10/30/17.
 */
/*
When nyou call the RecorderSettings class inside of another method use the follow syntax and perams
        this.recordingThread = new Thread();

        recorderSettings.setBandData(is a parameter and string array which is modeled in BandInfo);
        recorderSettings.setAudioSavePathInDevice(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings" + recorderSettings.getPathname());
        recorderSettings.setPathname(createAudioPathname(bandData, recorderSettings.getEXTENSION()));
        recorderSettings.setAudioRecord(new AudioRecord(recorderSettings.getRecorderAudioSource(),
                                                        recorderSettings.getRecordingSampleRate(),
                                                        recorderSettings.getRecordingChannels(),
                                                        recorderSettings.getRecordingAudioEncoding(),
        recorderSettings.getBufferElementsToRec() * recorderSettings.getBytesPerElement()));
    }

 */


public class RecorderSettings {

    private Date startTime;
    private Date endTime;
    private long duration;

    private String audioSavePathInDevice;
    private MediaRecorder mediaRecorder;
    private Random random;

    private String[] bandData;

    private static int recorderAudioSource;
    private static int recorderOutputFormat;
    private static int recordingEncodingBitRate;
    private static int recordingSampleRate;
    private static int recordingChannels;
    @RequiresApi(LOLLIPOP) private static int recordingAudioEncoding;
    private AudioRecord audioRecord;
    private boolean isRecording = false;

    private final String EXTENSION = ".aac";
    private String pathname;

    public RecorderSettings() {
        /**
         *
         */
        this.startTime = null;
        this.endTime = null;
        this.random = new Random();
        recorderAudioSource = MediaRecorder.AudioSource.DEFAULT;
        recorderOutputFormat = MediaRecorder.OutputFormat.AAC_ADTS;
        recordingEncodingBitRate = 16;
        recordingSampleRate = 12000;
        recordingChannels = AudioFormat.CHANNEL_IN_MONO;
        recordingAudioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        this.mediaRecorder = mediaRecorderReady();
    }

    public RecorderSettings(String audioSavePathInDevice, String[] bandData, AudioRecord audioRecord, String pathname) {
        /**
         *
         */
        this.startTime = null;
        this.endTime = null;
        this.audioSavePathInDevice = audioSavePathInDevice;
        this.random = new Random();
        this.bandData = bandData;
        recorderAudioSource = MediaRecorder.AudioSource.DEFAULT;
        recorderOutputFormat = MediaRecorder.OutputFormat.AAC_ADTS;
        recordingEncodingBitRate = 16;
        recordingSampleRate = 12000;
        recordingChannels = AudioFormat.CHANNEL_IN_MONO;
        recordingAudioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        this.audioRecord = audioRecord;
        this.pathname = pathname;
        this.mediaRecorder = mediaRecorderReady(recorderAudioSource, recorderOutputFormat, recordingEncodingBitRate,
                recordingSampleRate, recordingChannels, recordingAudioEncoding);
    }

    public void setRecorderOptions(int recorderAudioSource, int recorderOutputFormat, int recordingEncodingBitRate,
                                    int recordingSampleRate, int recordingChannels, int recordingAudioEncoding) {
        /**
         *
         */
        this.recorderAudioSource = recorderAudioSource;
        this.recorderOutputFormat = recorderOutputFormat;
        this.recordingEncodingBitRate = recordingEncodingBitRate;
        this.recordingSampleRate = recordingSampleRate;
        this.recordingChannels = recordingChannels;
        this.recordingAudioEncoding = recordingAudioEncoding;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MediaRecorder mediaRecorderReady() {
        /**
         *
         */
        this.mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(recorderAudioSource);
        mediaRecorder.setOutputFormat(recorderOutputFormat);
        mediaRecorder.setAudioEncoder(recordingAudioEncoding);
        mediaRecorder.setAudioChannels(recordingChannels);
        mediaRecorder.setAudioEncodingBitRate(recordingEncodingBitRate);
        mediaRecorder.setAudioSamplingRate(recordingSampleRate);
        mediaRecorder.setOutputFile(audioSavePathInDevice);
        Log.d("recorderAudioSource: ", "" + recorderAudioSource);
        Log.d("recorderOutputFormat: ", "" + recorderOutputFormat);
        Log.d("recorderAudioEncoding: ", "" + recordingAudioEncoding);
        Log.d("recordingChannels: ", "" + recordingChannels);
        Log.d("recorderEncodingBtRte: ", "" + recordingEncodingBitRate);
        Log.d("recordingSampleRate: ", "" + recordingSampleRate);
        Log.d("audioSavePathInDev: ", "" + audioSavePathInDevice);

        return this.mediaRecorder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MediaRecorder mediaRecorderReady(int recorderAudioSource, int recorderOutputFormat, int recordingEncodingBitRate,
                                            int recordingSampleRate, int recordingChannels, int recordingAudioEncoding) {
        /**
         *
         */
        this.mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(recorderAudioSource);
        mediaRecorder.setOutputFormat(recorderOutputFormat);
        mediaRecorder.setAudioEncoder(recordingAudioEncoding);
        mediaRecorder.setAudioChannels(recordingChannels);
        mediaRecorder.setAudioEncodingBitRate(recordingEncodingBitRate);
        mediaRecorder.setAudioSamplingRate(recordingSampleRate);
        mediaRecorder.setOutputFile(audioSavePathInDevice);

        return this.mediaRecorder;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        if (duration > 0) {
            return duration;
        } else if ((startTime != null) && (endTime != null)) {
            this.duration = endTime.getTime() - startTime.getTime();
            return duration;
        } else {
            return 0;
        }
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getAudioSavePathInDevice() {
        return audioSavePathInDevice;
    }

    public void setAudioSavePathInDevice(String audioSavePathInDevice) {
        this.audioSavePathInDevice = audioSavePathInDevice;
    }

    public MediaRecorder getMediaRecorder() {
        return mediaRecorder;
    }

    public void setMediaRecorder(MediaRecorder mediaRecorder) {
        this.mediaRecorder = mediaRecorder;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public String getRANDOM_AUDIO_FILE_NAME() {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    }

    public String[] getBandData() {
        return bandData;
    }

    public void setBandData(String[] bandData) {
        this.bandData = bandData;
    }

    public static int getRecorderAudioSource() {
        return recorderAudioSource;
    }

    public static void setRecorderAudioSource(int recorderAudioSource) {
        RecorderSettings.recorderAudioSource = recorderAudioSource;
    }

    public static int getRecorderOutputFormat() {
        return recorderOutputFormat;
    }

    public static void setRecorderOutputFormat(int recorderOutputFormat) {
        RecorderSettings.recorderOutputFormat = recorderOutputFormat;
    }

    public static int getRecordingEncodingBitRate() {
        return recordingEncodingBitRate;
    }

    public static void setRecordingEncodingBitRate(int recordingEncodingBitRate) {
        RecorderSettings.recordingEncodingBitRate = recordingEncodingBitRate;
    }

    public static int getRecordingSampleRate() {
        return recordingSampleRate;
    }

    public static void setRecordingSampleRate(int recordingSampleRate) {
        RecorderSettings.recordingSampleRate = recordingSampleRate;
    }

    public static int getRecordingChannels() {
        return recordingChannels;
    }

    public static void setRecordingChannels(int recordingChannels) {
        RecorderSettings.recordingChannels = recordingChannels;
    }

    public static int getRecordingAudioEncoding() {
        return recordingAudioEncoding;
    }

    public static void setRecordingAudioEncoding(int recordingAudioEncoding) {
        RecorderSettings.recordingAudioEncoding = recordingAudioEncoding;
    }

    public AudioRecord getAudioRecord() {
        return audioRecord;
    }

    public void setAudioRecord(AudioRecord audioRecord) {
        this.audioRecord = audioRecord;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public int getBufferElementsToRec() {
        return 1024;
    }

    public int getBytesPerElement() {
        return 2;
    }

    public String getEXTENSION() {
        return EXTENSION;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }
}

package com.draglabs.dsoundboy.dsoundboy.Models;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Date;
import java.util.Random;

import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * <p>The recording settings for the audio recorder</p>
 * <p>Created by davrukin on 10/30/17.</p>
 */

/*
When you call the RecorderModel class inside of another method use the follow syntax and perams
        this.recordingThread = new Thread();

        recorderSettings.setBandData(is a parameter and string array which is modeled in BandInfoModel);
        recorderSettings.setAudioSavePathInDevice(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dSoundBoyRecordings" + recorderSettings.getPathname());
        recorderSettings.setPathname(createAudioPathname(bandData, recorderSettings.getEXTENSION()));
        recorderSettings.setAudioRecord(new AudioRecord(recorderSettings.getRecorderAudioSource(),
                                                        recorderSettings.getRecordingSampleRate(),
                                                        recorderSettings.getRecordingChannels(),
                                                        recorderSettings.getRecordingAudioEncoding(),
        recorderSettings.getBufferElementsToRec() * recorderSettings.getBytesPerElement()));
    }

 */


public class RecorderModel {

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

    /**
     * <p>Default no-arg constructor the Recorder Model</p>
     * <p>Output format: mp4; Encoding bit rate: 24; Sample rate: 44100; Channels: 1; Audio encoder; AAC</p>
     */
    public RecorderModel() {
        this.startTime = null;
        this.endTime = null;
        this.random = new Random();
        this.recorderAudioSource = MediaRecorder.AudioSource.MIC;
        this.recorderOutputFormat = MediaRecorder.OutputFormat.MPEG_4;
        this.recordingEncodingBitRate = 16;
        this.recordingSampleRate = 44100;
        this.recordingChannels = 1;
        this.recordingAudioEncoding = MediaRecorder.AudioEncoder.AAC;
        //this.mediaRecorder = mediaRecorderReady();
    }

    /**
     * <p>Constructor for the Recorder Model</p>
     * <p>Output format: mp4; Encoding bit rate: 24; Sample rate: 44100; Channels: 1; Audio encoder; AAC</p>
     * @param audioSavePathInDevice the local path where the file is saved
     * @param bandData the band's data
     * @param audioRecord the AudioRecord object
     * @param pathname the name of the file
     */
    public RecorderModel(String audioSavePathInDevice, String[] bandData, AudioRecord audioRecord, String pathname) {
        this.startTime = null;
        this.endTime = null;
        this.audioSavePathInDevice = audioSavePathInDevice;
        this.random = new Random();
        this.bandData = bandData;
        this.recorderAudioSource = MediaRecorder.AudioSource.MIC;
        this.recorderOutputFormat = MediaRecorder.OutputFormat.MPEG_4;
        this.recordingEncodingBitRate = 24; // was 192000
        this.recordingSampleRate = 44100;
        this.recordingChannels = 1;
        this.recordingAudioEncoding = MediaRecorder.AudioEncoder.AAC;
        this.audioRecord = audioRecord;
        this.pathname = pathname;
        this.mediaRecorder = mediaRecorderReady(recorderAudioSource, recorderOutputFormat, recordingEncodingBitRate,
                recordingSampleRate, recordingChannels, recordingAudioEncoding);
    }

    /**
     * Sets the recorder options
     * @param recorderAudioSource the audio source set by "MediaRecorder.AudioSource.***"
     * @param recorderOutputFormat the output format set by "MediaRecorder.OutputFormat.***"
     * @param recordingEncodingBitRate the encoding bit rate in bits
     * @param recordingSampleRate the sample rate in Hz
     * @param recordingChannels amount of recording channels
     * @param recordingAudioEncoding the encoding set by "MediaRecorder.AudioEncoder.***"
     */
    public void setRecorderOptions(int recorderAudioSource, int recorderOutputFormat, int recordingEncodingBitRate,
                                    int recordingSampleRate, int recordingChannels, int recordingAudioEncoding) {
        this.recorderAudioSource = recorderAudioSource;
        this.recorderOutputFormat = recorderOutputFormat;
        this.recordingEncodingBitRate = recordingEncodingBitRate;
        this.recordingSampleRate = recordingSampleRate;
        this.recordingChannels = recordingChannels;
        this.recordingAudioEncoding = recordingAudioEncoding;
    }

    /**
     * Prepares a MediaRecorder object for recording
     * @return a new MediaRecorder object
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MediaRecorder mediaRecorderReady() {
        this.mediaRecorder = new MediaRecorder();

        this.mediaRecorder.setAudioSource(recorderAudioSource);
        this.mediaRecorder.setOutputFormat(recorderOutputFormat);
        this.mediaRecorder.setAudioEncoder(recordingAudioEncoding);
        this.mediaRecorder.setAudioChannels(recordingChannels);
        this.mediaRecorder.setAudioEncodingBitRate(recordingEncodingBitRate);
        this.mediaRecorder.setAudioSamplingRate(recordingSampleRate);
        this.mediaRecorder.setOutputFile(audioSavePathInDevice);
        Log.d("recorderAudioSource: ", "" + recorderAudioSource);
        Log.d("recorderOutputFormat: ", "" + recorderOutputFormat);
        Log.d("recorderAudioEncoding: ", "" + recordingAudioEncoding);
        Log.d("recordingChannels: ", "" + recordingChannels);
        Log.d("recorderEncodingBtRte: ", "" + recordingEncodingBitRate);
        Log.d("recordingSampleRate: ", "" + recordingSampleRate);
        Log.d("audioSavePathInDev: ", "" + audioSavePathInDevice);

        return this.mediaRecorder;
    }

    /**
     * Prepares a MediaRecorder object for recording
     * @param recorderAudioSource the audio source set by "MediaRecorder.AudioSource.***"
     * @param recorderOutputFormat the output format set by "MediaRecorder.OutputFormat.***"
     * @param recordingEncodingBitRate the encoding bit rate in bits
     * @param recordingSampleRate the sample rate in Hz
     * @param recordingChannels amount of recording channels
     * @param recordingAudioEncoding the encoding set by "MediaRecorder.AudioEncoder.***"
     * @return a new MediaRecorder object
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MediaRecorder mediaRecorderReady(int recorderAudioSource, int recorderOutputFormat, int recordingEncodingBitRate,
                                            int recordingSampleRate, int recordingChannels, int recordingAudioEncoding) {

        this.mediaRecorder = new MediaRecorder();

        this.mediaRecorder.setAudioSource(recorderAudioSource);
        this.mediaRecorder.setOutputFormat(recorderOutputFormat);
        this.mediaRecorder.setAudioEncoder(recordingAudioEncoding);
        this.mediaRecorder.setAudioChannels(recordingChannels);
        this.mediaRecorder.setAudioEncodingBitRate(recordingEncodingBitRate);
        this.mediaRecorder.setAudioSamplingRate(recordingSampleRate);
        this.mediaRecorder.setOutputFile(audioSavePathInDevice);

        return this.mediaRecorder;
    }

    /**
     * Gets the recording's start time
     * @return the recording's start time
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Sets the recording's start time
     * @param startTime the recording's start time
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the recording's end time
     * @return the recording's end time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Sets the recording's end time
     * @param endTime the recording's end time
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Calculates the duration of the recording in ms
     * @return the duration of the recording in ms
     */
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

    /**
     * Sets the duration of the recording in ms
     * @param duration the duration of the recording in ms
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * Gets where the file is being saved in the device
     * @return the save path
     */
    public String getAudioSavePathInDevice() {
        return audioSavePathInDevice;
    }

    /**
     * Sets where the file is being saved in the device
     * @param audioSavePathInDevice the save path
     */
    public void setAudioSavePathInDevice(String audioSavePathInDevice) {
        this.audioSavePathInDevice = audioSavePathInDevice;
    }

    /**
     * Gets the MediaRecorder object
     * @return the MediaRecorder object
     */
    public MediaRecorder getMediaRecorder() {
        return mediaRecorder;
    }

    /**
     * Sets the MediaRecorder object
     * @param mediaRecorder the MediaRecorder object
     */
    public void setMediaRecorder(MediaRecorder mediaRecorder) {
        this.mediaRecorder = mediaRecorder;
    }

    /**
     * Gets the random object
     * @return the random object
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Sets the random object
     * @param random the random object
     */
    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Gets the random audio file name
     * @return the random audio file name
     */
    public String getRANDOM_AUDIO_FILE_NAME() {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    }

    /**
     * Gets the band data
     * @return the band data
     */
    public String[] getBandData() {
        return bandData;
    }

    /**
     * Sets the band data
     * @param bandData the band data
     */
    public void setBandData(String[] bandData) {
        this.bandData = bandData;
    }

    /**
     * Gets the recorder audio source
     * @return the recorder audio source
     */
    public static int getRecorderAudioSource() {
        return recorderAudioSource;
    }

    /**
     * Sets the recorder audio source
     * @param recorderAudioSource the audio source set by "MediaRecorder.AudioSource.***"
     */
    public static void setRecorderAudioSource(int recorderAudioSource) {
        RecorderModel.recorderAudioSource = recorderAudioSource;
    }

    /**
     * Gets the recorder output format
     * @return the recorder output format
     */
    public static int getRecorderOutputFormat() {
        return recorderOutputFormat;
    }

    /**
     * Sets the recorder output format
     * @param recorderOutputFormat the output format set by "MediaRecorder.OutputFormat.***"
     */
    public static void setRecorderOutputFormat(int recorderOutputFormat) {
        RecorderModel.recorderOutputFormat = recorderOutputFormat;
    }

    /**
     * Gets the recording encoding bit rate
     * @return the recording encoding bit rate
     */
    public static int getRecordingEncodingBitRate() {
        return recordingEncodingBitRate;
    }

    /**
     * Sets the recording encoding bit rate
     * @param recordingEncodingBitRate the encoding bit rate in bits
     */
    public static void setRecordingEncodingBitRate(int recordingEncodingBitRate) {
        RecorderModel.recordingEncodingBitRate = recordingEncodingBitRate;
    }

    /**
     * Gets the recording sample rate
     * @return the recording sample rate
     */
    public static int getRecordingSampleRate() {
        return recordingSampleRate;
    }

    /**
     * Sets the recording sample rate
     * @param recordingSampleRate the sample rate in Hz
     */
    public static void setRecordingSampleRate(int recordingSampleRate) {
        RecorderModel.recordingSampleRate = recordingSampleRate;
    }

    /**
     * Gets the amount of recording channels
     * @return the amount of recording channels
     */
    public static int getRecordingChannels() {
        return recordingChannels;
    }

    /**
     * Sets the amount of recording channels
     * @param recordingChannels the amount of recording channels
     */
    public static void setRecordingChannels(int recordingChannels) {
        RecorderModel.recordingChannels = recordingChannels;
    }

    /**
     * Gets the recording audio encoding
     * @return the recording audio encoding
     */
    public static int getRecordingAudioEncoding() {
        return recordingAudioEncoding;
    }

    /**
     * Sets the recording audio encoding
     * @param recordingAudioEncoding the encoding set by "MediaRecorder.AudioEncoder.***"
     */
    public static void setRecordingAudioEncoding(int recordingAudioEncoding) {
        RecorderModel.recordingAudioEncoding = recordingAudioEncoding;
    }

    /**
     * Gets the AudioRecord object
     * @return the AudioRecord object
     */
    public AudioRecord getAudioRecord() {
        return audioRecord;
    }

    /**
     * Sets the AudioRecord object
     * @param audioRecord the AudioRecord object
     */
    public void setAudioRecord(AudioRecord audioRecord) {
        this.audioRecord = audioRecord;
    }

    /**
     * Tells whether or not it is currently recording
     * @return True is yes or False if no
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * Sets whether or not it is currently recording
     * @param recording True if yes or False if no
     */
    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    /**
     * Gets the buffer elements to record
     * @return 1024
     */
    public int getBufferElementsToRec() {
        return 1024;
    }

    /**
     * Gets the bytes per element
     * @return 2
     */
    public int getBytesPerElement() {
        return 2;
    }

    /**
     * Gets the extension of the file
     * @return the extension of the file
     */
    public String getEXTENSION() {
        return EXTENSION;
    }

    /**
     * Gets the pathname of the file
     * @return the pathname of the file
     */
    public String getPathname() {
        return pathname;
    }

    /**
     * Sets the pathname of the file
     * @param pathname the pathname of the file
     */
    public void setPathname(String pathname) {
        this.pathname = pathname;
    }
}

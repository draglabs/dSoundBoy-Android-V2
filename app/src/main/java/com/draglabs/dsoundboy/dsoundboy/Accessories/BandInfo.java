package com.draglabs.dsoundboy.dsoundboy.Accessories;

/**
 * Created by davrukin on 8/16/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class BandInfo {

    private String artistEmail;
    private String recordingDescription;
    private String artistName;
    private String recordingVenue;

    public BandInfo() {
    }

    public BandInfo(String artistEmail, String recordingDescription, String artistName, String recordingVenue) {
        this.artistEmail = artistEmail;
        this.recordingDescription = recordingDescription;
        this.artistName = artistName;
        this.recordingVenue = recordingVenue;
    }

    public String getArtistEmail() {
        return artistEmail;
    }

    public void setArtistEmail(String artistEmail) {
        this.artistEmail = artistEmail;
    }

    public String getRecordingDescription() {
        return recordingDescription;
    }

    public void setRecordingDescription(String recordingDescription) {
        this.recordingDescription = recordingDescription;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getRecordingVenue() {
        return recordingVenue;
    }

    public void setRecordingVenue(String recordingVenue) {
        this.recordingVenue = recordingVenue;
    }
}

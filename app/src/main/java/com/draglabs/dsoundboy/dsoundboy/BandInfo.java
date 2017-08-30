package com.draglabs.dsoundboy.dsoundboy;

/**
 * Created by davrukin on 8/16/17.
 */

public class BandInfo {

    private String bandEmail;
    private String bandRecordingDescription;
    private String bandName;
    private String bandVenue;

    public BandInfo() {
    }

    public BandInfo(String bandEmail, String bandRecordingDescription, String bandName, String bandVenue) {
        this.bandEmail = bandEmail;
        this.bandRecordingDescription = bandRecordingDescription;
        this.bandName = bandName;
        this.bandVenue = bandVenue;
    }

    public String getBandEmail() {
        return bandEmail;
    }

    public void setBandEmail(String bandEmail) {
        this.bandEmail = bandEmail;
    }

    public String getBandRecordingDescription() {
        return bandRecordingDescription;
    }

    public void setBandRecordingDescription(String bandRecordingDescription) {
        this.bandRecordingDescription = bandRecordingDescription;
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public String getBandVenue() {
        return bandVenue;
    }

    public void setBandVenue(String bandVenue) {
        this.bandVenue = bandVenue;
    }
}

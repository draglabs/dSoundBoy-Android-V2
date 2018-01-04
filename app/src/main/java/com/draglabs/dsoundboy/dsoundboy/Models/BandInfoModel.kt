package com.draglabs.dsoundboy.dsoundboy.Models

/**
 *
 * Holds info of the band
 *
 * Created by davrukin on 8/16/17.
 */

class BandInfoModel {

    var artistEmail: String? = null
    var recordingDescription: String? = null
    var artistName: String? = null
    var recordingVenue: String? = null

    constructor(artistEmail: String, recordingDescription: String, artistName: String, recordingVenue: String) {
        this.artistEmail = artistEmail
        this.recordingDescription = recordingDescription
        this.artistName = artistName
        this.recordingVenue = recordingVenue
    }
}

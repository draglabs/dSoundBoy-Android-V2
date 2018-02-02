/*
 * Daniel Avrukin of Drag Labs. Copyright (c) 2016-2018. All Rights Reserved.
 */

package com.draglabs.dsoundboy.dsoundboy.Tasks

/**
 * Created by davrukin on 2/1/2018.
 * @author Daniel Avrukin
 */
class OfflineUploader {

    // add a flag to each file, says if boolean was uploaded or not
    // api call says that if there is a link, then it has been uploaded
    // before uploading the recording on clicking stop, check the setting
        // if it's offline-only, defer the file and add it to a list of files to be uploaded later

    // upon opening the testnavactivity, check the setting
        // if wifi-only is off
            // check wifi status to make sure it's on, then upload
            // read text file of files
            // if there are any to upload
                // do that in kotlin suspend/async
            // otherwise
        // if wifi-only is on
            // save recording, write the filename/filepath to a text file, and don't upload
            // upload only if wifi is on
}
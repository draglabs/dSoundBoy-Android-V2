package com.draglabs.dsoundboy.dsoundboy.Utils

import android.content.Context
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager
import com.amazonaws.mobileconnectors.cognito.Dataset
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.facebook.AccessToken
import java.util.*

/**
 * Created by davrukin on 8/25/17.
 */

// https://docs.aws.amazon.com/mobile/sdkforandroid/developerguide/setup.html

@Deprecated("")
class AWSutils(context: Context) {

    var credentialsProvider: CognitoCachingCredentialsProvider? = null
        private set
    private var syncClient: CognitoSyncManager? = null
    private val dataset: Dataset? = null
    private var logins: MutableMap<String, String>? = null

    val amazonS3Client: AmazonS3Client
        get() = AmazonS3Client(credentialsProvider)

    val token: String
        get() = credentialsProvider!!.token

    val sessionToken: String
        get() = credentialsProvider!!.credentials.sessionToken

    val awsAccessKeyID: String
        get() = credentialsProvider!!.credentials.awsAccessKeyId

    val awsSecretKey: String
        get() = credentialsProvider!!.credentials.awsSecretKey

    val identityId: String
        get() = credentialsProvider!!.identityId

    init {
        val thread = Thread {
            credentialsProvider = CognitoCachingCredentialsProvider(context, "us-east-1:1669047e-58e5-41bc-8866-4e9ff2dd86b7", Regions.US_EAST_1)
            syncClient = CognitoSyncManager(context, Regions.US_EAST_1, credentialsProvider!!)

            /*dataset = syncClient.openOrCreateDataset("myDataset");
            dataset.put("myKey", "myValue");
            dataset.synchronize(new DefaultSyncCallback() {
                @Override
                public void onSuccess(Dataset dataset, List newRecords) {
                    // handler code here
                }
            });*/

            // TODO: authenticate with Facebook: https://docs.aws.amazon.com/cognito/latest/developerguide/facebook.html

            logins = HashMap()
            logins!!.put("graph.facebook.com", AccessToken.getCurrentAccessToken().token)
            credentialsProvider!!.logins = logins
            credentialsProvider!!.refresh()
        }
        thread.start()
    }

}

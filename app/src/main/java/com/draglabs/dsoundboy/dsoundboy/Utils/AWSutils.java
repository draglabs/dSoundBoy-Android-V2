package com.draglabs.dsoundboy.dsoundboy.Utils;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.facebook.AccessToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by davrukin on 8/25/17.
 */

// https://docs.aws.amazon.com/mobile/sdkforandroid/developerguide/setup.html

@SuppressWarnings("DefaultFileTemplate")
public class AWSutils {

    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager syncClient;
    private Dataset dataset;
    private Map<String, String> logins;

    public AWSutils(final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                credentialsProvider = new CognitoCachingCredentialsProvider(context, "us-east-1:1669047e-58e5-41bc-8866-4e9ff2dd86b7", Regions.US_EAST_1);
                syncClient = new CognitoSyncManager(context, Regions.US_EAST_1, credentialsProvider);

                /*dataset = syncClient.openOrCreateDataset("myDataset");
                dataset.put("myKey", "myValue");
                dataset.synchronize(new DefaultSyncCallback() {
                    @Override
                    public void onSuccess(Dataset dataset, List newRecords) {
                        // handler code here
                    }
                });*/

                // TODO: authenticate with Facebook: https://docs.aws.amazon.com/cognito/latest/developerguide/facebook.html

                logins = new HashMap<>();
                logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().getToken());
                credentialsProvider.setLogins(logins);
                credentialsProvider.refresh();
            }
        });
        thread.start();
    }

    public AmazonS3Client getAmazonS3Client() {
        return new AmazonS3Client(credentialsProvider);
    }

    public CognitoCachingCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public String getToken() {
        return credentialsProvider.getToken();
    }

    public String getSessionToken() {
        return credentialsProvider.getCredentials().getSessionToken();
    }

    public String getAWSAccessKeyID() {
        return credentialsProvider.getCredentials().getAWSAccessKeyId();
    }

    public String getAWSSecretKey() {
        return credentialsProvider.getCredentials().getAWSSecretKey();
    }

    public String getIdentityId() {
        return credentialsProvider.getIdentityId();
    }

}

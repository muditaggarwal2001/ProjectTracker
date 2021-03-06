package com.example.mudit.projecttracker;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

/**
 * Created by Mudit on 24-03-2018.
 */

public class awsManager {

    private AmazonS3 amazonS3client;
    private TransferUtility transferUtility;
    Context context;

    public awsManager(Context c)
    {
        context = c;
        // callback method to call credentialsProvider method.
        s3credentialProvider();

        // callback method to call the setTransferUtility method
        setTransferUtility();
    }

    private void setTransferUtility() {
        transferUtility = new TransferUtility(amazonS3client,context);
    }

    public void s3credentialProvider(){
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:a5675ad9-0c6c-4288-b519-58935fc82ccf", // Identity pool ID
                Regions.US_EAST_1 // Region
                 );
        createAmazonS3Client(credentialsProvider);
    }

    private void createAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider) {
        amazonS3client = new AmazonS3Client(credentialsProvider);
        amazonS3client.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    public void deleteFile(File file){
        amazonS3client.deleteObject(Utils.bucket,file.getName());
    }

    public TransferUtility getTransferUtility() {
        return transferUtility;
    }
}

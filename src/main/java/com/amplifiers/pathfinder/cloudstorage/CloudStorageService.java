package com.amplifiers.pathfinder.cloudstorage;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.util.List;

@Component
public class CloudStorageService {
    private static final S3Client s3Client;
    private static final String bucketName = "pathfinder-bucket";

    static  {
        String accessKey = "003b877a457d48f0000000003";
        String secretKey = "K003v/skEFnbcQwhpzHfl8kfw79tcr8";
        final String END_POINT = "https://s3.eu-central-003.backblazeb2.com";
        AwsSessionCredentials awsCreds = AwsSessionCredentials.create(accessKey, secretKey, "");

        s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .endpointOverride(URI.create(END_POINT))
                .region(Region.of("eu-central-003")).build();
    }

    public static String uploadFile(byte[] fileData, String keyName) {

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(keyName).build();
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
            
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return "nothing";
    }

    public static byte[] getFile(String keyName){
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
            return objectBytes.asByteArray();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public static void clearBucket(String bucketNameClear) {
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(bucketNameClear)
                    .build();

            ListObjectsResponse res = s3Client.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketNameClear)
                        .key(myValue.key()).build();
                s3Client.deleteObject(deleteObjectRequest);
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }
}

package com.amplifiers.pathfinder.cloudstorage;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
public class CloudStorageService {

    private static final S3AsyncClient s3AsyncClient;
    private static final String bucketName = "pathfinder-bucket";
    private static final String accessKey = "003b877a457d48f0000000003";
    private static final String secretKey = "K003v/skEFnbcQwhpzHfl8kfw79tcr8";
    private static final String END_POINT = "https://s3.eu-central-003.backblazeb2.com";

    static {
        AwsSessionCredentials awsCreds = AwsSessionCredentials.create(accessKey, secretKey, "");

        s3AsyncClient = S3AsyncClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .endpointOverride(URI.create(END_POINT))
            .region(Region.of("eu-central-003"))
            .build();
    }

    public static void uploadFile(byte[] fileData, String keyName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(keyName).build();

            s3AsyncClient.putObject(putObjectRequest, AsyncRequestBody.fromBytes(fileData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getFile(String keyName) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder().key(keyName).bucket(bucketName).build();

            return s3AsyncClient.getObject(objectRequest, AsyncResponseTransformer.toBytes()).thenApply(ResponseBytes::asByteArray).join();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return null;
    }

    public static PresignedUrlInfo createPresignedGetUrl(String keyName) {
        try (
            S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKey, secretKey, "")))
                .endpointOverride(URI.create(END_POINT))
                .region(Region.of("eu-central-003"))
                .build()
        ) {
            GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(bucketName).key(keyName).build();

            Integer duration = 10;
            OffsetDateTime expires = OffsetDateTime.now().plusMinutes(10);
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(duration)) // The URL will expire in 10 minutes.
                .getObjectRequest(objectRequest)
                .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

            return PresignedUrlInfo.builder().presignedUrl(presignedRequest.url()).expires(expires).build();
        } catch (Exception E) {
            E.printStackTrace();
            return null;
        }
    }

    public static void deleteFile(String keyName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build();
            s3AsyncClient.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public static void clearBucket(String bucketNameClear) {
        try {
            ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(bucketNameClear).build();

            CompletableFuture<ListObjectsResponse> future = s3AsyncClient.listObjects(listObjects);

            future.thenAccept(res -> {
                List<S3Object> objects = res.contents();
                for (S3Object myValue : objects) {
                    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketNameClear)
                        .key(myValue.key())
                        .build();
                    s3AsyncClient.deleteObject(deleteObjectRequest);
                }
            });
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }
}

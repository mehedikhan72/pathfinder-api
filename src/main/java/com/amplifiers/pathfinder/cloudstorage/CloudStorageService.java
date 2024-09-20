package com.amplifiers.pathfinder.cloudstorage;

import java.net.URI;
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

    private static final S3AsyncClient S3_ASYNC_CLIENT;
    private static final String BUCKET_NAME = "pathfinder-bucket";
    private static final String ACCESS_KEY = "003b877a457d48f0000000003";
    private static final String SECRET_KEY = "K003v/skEFnbcQwhpzHfl8kfw79tcr8";
    private static final String END_POINT = "https://s3.eu-central-003.backblazeb2.com";
    private static final int URL_EXPIRATION_DURATION_MINUTES = 10;

    static {
        AwsSessionCredentials awsCreds = AwsSessionCredentials.create(ACCESS_KEY, SECRET_KEY, "");

        S3_ASYNC_CLIENT = S3AsyncClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .endpointOverride(URI.create(END_POINT))
            .region(Region.of("eu-central-003"))
            .build();
    }

    public static void uploadFile(byte[] fileData, String keyName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(BUCKET_NAME).key(keyName).build();

            S3_ASYNC_CLIENT.putObject(putObjectRequest, AsyncRequestBody.fromBytes(fileData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getFile(String keyName) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder().key(keyName).bucket(BUCKET_NAME).build();

            return S3_ASYNC_CLIENT.getObject(objectRequest, AsyncResponseTransformer.toBytes()).thenApply(ResponseBytes::asByteArray).join();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        return null;
    }

    public static PresignedUrlInfo createPresignedGetUrl(String keyName) {
        try (
            S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsSessionCredentials.create(ACCESS_KEY, SECRET_KEY, "")))
                .endpointOverride(URI.create(END_POINT))
                .region(Region.of("eu-central-003"))
                .build()
        ) {
            GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(BUCKET_NAME).key(keyName).build();

            OffsetDateTime expires = OffsetDateTime.now().plusMinutes(URL_EXPIRATION_DURATION_MINUTES);
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(URL_EXPIRATION_DURATION_MINUTES)) // The URL will expire in 10 minutes.
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
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(keyName).build();
            S3_ASYNC_CLIENT.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    public static void clearBucket(String bucketNameClear) {
        try {
            ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(bucketNameClear).build();

            CompletableFuture<ListObjectsResponse> future = S3_ASYNC_CLIENT.listObjects(listObjects);

            future.thenAccept(res -> {
                List<S3Object> objects = res.contents();
                for (S3Object myValue : objects) {
                    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketNameClear)
                        .key(myValue.key())
                        .build();
                    S3_ASYNC_CLIENT.deleteObject(deleteObjectRequest);
                }
            });
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }
}

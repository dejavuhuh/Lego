package com.github.dejavuhuh.lego.file.s3;

import com.github.dejavuhuh.lego.file.FileStorage;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.InputStream;
import java.net.URI;

/**
 * @author wu.yue
 * @since 2023/12/30 15:18
 */
public class S3FileStorage implements FileStorage {

    final S3Client client;
    final S3Config config;

    public S3FileStorage(S3Config config) {
        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey());
        this.client =
                S3Client.builder()
                        .region(Region.of(config.getRegion()))
                        .endpointOverride(URI.create(config.getEndpoint()))
                        .credentialsProvider(StaticCredentialsProvider.create(credentials))
                        .build();
        this.config = config;
    }

    @Override
    public String put(InputStream inputStream) {
        PutObjectRequest putObjectRequest =
                PutObjectRequest.builder().bucket(config.getBucket()).build();

        PutObjectResponse putObjectResponse =
                client.putObject(
                        putObjectRequest,
                        software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                                inputStream, -1));

        // Return the key if the upload is successful
        return null;
    }

    @Override
    public InputStream get(String id) {
        return null;
    }

    @Override
    public void delete(String id) {
        client.deleteObject(builder -> builder.bucket(config.getBucket()).key(id));
    }
}

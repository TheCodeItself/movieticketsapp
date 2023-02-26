package com.larasierra.movietickets.moviemedia.external.objectstorage;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

@Repository
public class MovieMediaS3Repository {

    private static final String MEDIA_BUCKET_NAME = "mediaticketsapp";
    private final S3Client s3Client;

    public MovieMediaS3Repository(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void upload(String key, long contentLength, InputStream is) throws IOException {
        var request = PutObjectRequest.builder()
                .bucket(MEDIA_BUCKET_NAME)
                .key(key)
                .build();

        try (is) {
            s3Client.putObject(request, RequestBody.fromInputStream(is, contentLength));
        }
    }

    public ResponseInputStream<GetObjectResponse> download(String key) {
        var request = GetObjectRequest.builder()
                .bucket(MEDIA_BUCKET_NAME)
                .key(key)
                .build();

        return s3Client.getObject(request);
    }

    public void delete(String key) {
        var request = DeleteObjectRequest.builder()
                .bucket(MEDIA_BUCKET_NAME)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }
}

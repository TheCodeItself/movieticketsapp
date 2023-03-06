package com.larasierra.movietickets.moviemedia.external.objectstorage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3BeanConfig {

    private final Environment environment;

    public S3BeanConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public S3Client s3Client() {
        var awsBasicCredentials = AwsBasicCredentials.create(
                environment.getProperty("MOVIE_TICKETS_AWS_MEDIA_SERVICE_ACCESS_KEY"),
                environment.getProperty("MOVIE_TICKETS_AWS_MEDIA_SERVICE_SECRET_KEY")
        );

        var credentialsProvider = StaticCredentialsProvider.create(awsBasicCredentials);

        return S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }

}

package com.larasierra.movietickets.user.external.dynamodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbConfig {
    private final Environment environment;

    public DynamoDbConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        var awsBasicCredentials = AwsBasicCredentials.create(
                environment.getProperty("MOVIE_TICKETS_AWS_USER_SERVICE_ACCESS_KEY"),
                environment.getProperty("MOVIE_TICKETS_AWS_USER_SERVICE_SECRET_KEY")
        );

        var credentialsProvider = StaticCredentialsProvider.create(awsBasicCredentials);

        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient())
                .build();
    }
}

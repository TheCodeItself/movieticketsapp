package com.larasierra.movietickets.user.external.cognito;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
public class CognitoBeanConf {

    private final Environment environment;

    public CognitoBeanConf(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityClient() {
        var awsBasicCredentials = AwsBasicCredentials.create(
                environment.getProperty("MOVIE_TICKETS_AWS_USER_SERVICE_ACCESS_KEY"),
                environment.getProperty("MOVIE_TICKETS_AWS_USER_SERVICE_SECRET_KEY")
        );

        var credentialsProvider = StaticCredentialsProvider.create(awsBasicCredentials);

        return CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }

}

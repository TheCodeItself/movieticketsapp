package com.larasierra.movietickets.user.external.cognito;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

@Repository
public class AppUserCognitoRepository {

    @Value("${larasierra.cognito.userpool}")
    private String USER_POOL_ID;

    private final CognitoIdentityProviderClient cognitoClient;

    public AppUserCognitoRepository(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    @Deprecated(forRemoval = true)
    public void changeOrganizerId(String username, String organizerId) {
        var organizerIdAttribute = AttributeType.builder()
                .name("custom:organizerId")
                .value(organizerId)
                .build();

        cognitoClient.adminUpdateUserAttributes(builder ->
            builder.userAttributes(organizerIdAttribute)
                    .username(username)
                    .userPoolId(USER_POOL_ID)
        );
    }

    public void addUserToGroup(String username, String groupName) {
        cognitoClient.adminAddUserToGroup(builder ->
            builder.groupName(groupName)
                    .username(username)
                    .userPoolId(USER_POOL_ID)
        );
    }

    public void removeUserFromGroup(String username, String groupName) {
        cognitoClient.adminRemoveUserFromGroup(builder ->
            builder.groupName(groupName)
                    .username(username)
                    .userPoolId(USER_POOL_ID)
        );
    }
}

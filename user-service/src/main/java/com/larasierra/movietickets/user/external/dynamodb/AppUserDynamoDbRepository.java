package com.larasierra.movietickets.user.external.dynamodb;

import com.larasierra.movietickets.shared.exception.AppInternalErrorException;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.user.domain.AppUser;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.fromS;
import static software.amazon.awssdk.services.dynamodb.model.AttributeValue.fromSs;

@Repository
public class AppUserDynamoDbRepository {
    private final DynamoDbClient dynamoClient;
    private final DynamoDbEnhancedClient enhancedClient;

    private DynamoDbTable<AppUser> table;

    public AppUserDynamoDbRepository(DynamoDbClient dynamoClient, DynamoDbEnhancedClient enhancedClient) {
        this.dynamoClient = dynamoClient;
        this.enhancedClient = enhancedClient;
    }

    @PostConstruct
    public void init() {
        table = enhancedClient.table("AppUser", AppUser.SCHEMA);
    }

    public void create(AppUser appUser) {
        PutItemEnhancedRequest<AppUser> request = PutItemEnhancedRequest
                .builder(AppUser.class)
                .item(appUser)
                .conditionExpression(Expression.builder().expression("attribute_not_exists(userId)").build())
                .build();

        try {
            table.putItem(request);
        } catch (ConditionalCheckFailedException e) {
            throw new AppInternalErrorException();
        }
    }

    public Optional<AppUser> findById(String userId) {
        try {
            AppUser user = table.getItem(builder ->
                    builder.consistentRead(false)
                            .key(kb -> kb.partitionValue(userId))
            );
            return Optional.ofNullable(user);
        } catch(ResourceNotFoundException e) {
            return Optional.empty();
        } catch (SdkException e) {
            throw new AppInternalErrorException();
        }
    }

    public void addUserToGroup(String userId, String groupName) {
        var key = Map.of("userId", fromS(userId));

        var attributeNames = Map.of("#roles", "roles");
        var attributeValues = Map.of(":roleName", fromSs(List.of(groupName)));

        var request = UpdateItemRequest.builder()
                .conditionExpression("attribute_exists(userId)")
                .tableName("AppUser")
                .key(key)
                .updateExpression("ADD #roles :roleName")
                .expressionAttributeNames(attributeNames)
                .expressionAttributeValues(attributeValues)
                .build();

        try {
            dynamoClient.updateItem(request);
        } catch (ConditionalCheckFailedException e) {

        }
    }

    public void removeUserFromGroup(String userId, String groupName) {
        var key = Map.of("userId", fromS(userId));

        var attributeNames = Map.of("#roles", "roles");
        var attributeValues = Map.of(":roleName", fromSs(List.of(groupName)));

        var request = UpdateItemRequest.builder()
                .conditionExpression("attribute_exists(userId)")
                .tableName("AppUser")
                .key(key)
                .updateExpression("DELETE #roles :roleName")
                .expressionAttributeNames(attributeNames)
                .expressionAttributeValues(attributeValues)
                .build();

        dynamoClient.updateItem(request);
    }

    @Deprecated(forRemoval = true)
    public void changeOrganizerId(String userId, String organizerId) {
        var key = Map.of("userId", fromS(userId));

        var attributeNames = Map.of("#organizerId", "organizerId");
        var attributeValues = Map.of(":organizerId", fromS(organizerId));

        // TODO: 25/01/2023 it must return the user's new attribute values?

        var request = UpdateItemRequest.builder()
                .conditionExpression("attribute_exists(userId)")
                .tableName("AppUser")
                .key(key)
                .updateExpression("SET #organizerId = :organizerId")
                .expressionAttributeNames(attributeNames)
                .expressionAttributeValues(attributeValues)
                .build();

        try {
            dynamoClient.updateItem(request);
        } catch (ResourceNotFoundException e) {
            // TODO: 13/02/2023 this exception does not capture this type of "not found"
            throw new AppResourceNotFoundException();
        }
    }

}

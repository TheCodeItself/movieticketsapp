package com.larasierra.movietickets.user.external.dynamodb;

import com.larasierra.movietickets.user.domain.AppRole;
import com.larasierra.movietickets.shared.exception.AppInternalErrorException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.Optional;

@Repository
public class AppRoleDynamoDbRepository {
    private final DynamoDbEnhancedClient enhancedClient;

    private DynamoDbTable<AppRole> table;

    public AppRoleDynamoDbRepository(DynamoDbEnhancedClient enhancedClient) {
        this.enhancedClient = enhancedClient;
    }

    @PostConstruct
    public void init() {
        table = enhancedClient.table("AppRole", AppRole.SCHEMA);
    }

    public void create(AppRole appRole) {
        PutItemEnhancedRequest<AppRole> request = PutItemEnhancedRequest
                .builder(AppRole.class)
                .item(appRole)
                .conditionExpression(Expression.builder().expression("attribute_not_exists(roleId)").build())
                .build();

        try {
            table.putItem(request);
        } catch (ConditionalCheckFailedException e) {

        }
    }

    public Optional<AppRole> findById(String roleId) {
        try {
            AppRole role = table.getItem(builder ->
                    builder.consistentRead(false)
                            .key(kb -> kb.partitionValue(roleId))
            );
            return Optional.ofNullable(role);
        } catch(ResourceNotFoundException e) {
            return Optional.empty();
        } catch (SdkException e) {
            throw new AppInternalErrorException();
        }
    }
}

package com.larasierra.movietickets.user.domain;

import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UUID;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.OffsetDateTime;
import java.util.Set;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

    @ValidId
    @NotNull
    private String userId;

    @NotNull
    @Email
    private String email;

    @UUID
    @NotNull
    private String cognitoId;

    @NotNull
    private OffsetDateTime createdAt;

    @Size(min = 1)
    private Set<String> roles;

    public boolean hasRole(String roleId) {
        if (roles == null) {
            return false;
        }
        return roles.contains(roleId);
    }

    public static TableSchema<AppUser> SCHEMA;

    static {
        SCHEMA = TableSchema.builder(AppUser.class)
                .newItemSupplier(AppUser::new)
                .addAttribute(String.class, sa -> sa.name("userId")
                        .getter(AppUser::getUserId)
                        .setter(AppUser::setUserId)
                        .addTag(primaryPartitionKey()))
                .addAttribute(String.class, sa -> sa.name("email")
                        .getter(AppUser::getEmail)
                        .setter(AppUser::setEmail))
                .addAttribute(String.class, sa -> sa.name("cognitoId")
                        .getter(AppUser::getCognitoId)
                        .setter(AppUser::setCognitoId))
                .addAttribute(OffsetDateTime.class, sa -> sa.name("createdAt")
                        .getter(AppUser::getCreatedAt)
                        .setter(AppUser::setCreatedAt))
                .addAttribute(EnhancedType.setOf(String.class), sa -> sa.name("roles")
                        .getter(AppUser::getRoles)
                        .setter(AppUser::setRoles))
                .build();
    }
}

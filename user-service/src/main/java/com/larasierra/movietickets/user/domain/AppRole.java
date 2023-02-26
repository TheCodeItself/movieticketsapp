package com.larasierra.movietickets.user.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class AppRole {

    @Size(min = 2, max = 50)
    @NotNull
    String roleId;

    @Size(min = 2, max = 510)
    @NotNull
    String roleDescription;

    public static TableSchema<AppRole> SCHEMA;

    static {
        SCHEMA = TableSchema.builder(AppRole.class)
                .newItemSupplier(AppRole::new)
                .addAttribute(String.class, sa -> sa.name("roleId")
                        .getter(AppRole::getRoleId)
                        .setter(AppRole::setRoleId)
                        .addTag(primaryPartitionKey()))
                .addAttribute(String.class, sa -> sa.name("roleDescription")
                        .getter(AppRole::getRoleDescription)
                        .setter(AppRole::setRoleDescription))
                .build();
    }
}

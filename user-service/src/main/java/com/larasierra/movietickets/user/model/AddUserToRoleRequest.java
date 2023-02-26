package com.larasierra.movietickets.user.model;

import jakarta.validation.constraints.NotNull;

public record AddUserToRoleRequest(
    @NotNull
    String roleId
) {}

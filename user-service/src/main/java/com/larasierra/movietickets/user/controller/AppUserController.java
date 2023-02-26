package com.larasierra.movietickets.user.controller;

import com.larasierra.movietickets.shared.validation.ValidId;
import com.larasierra.movietickets.user.application.AppUserService;
import com.larasierra.movietickets.user.model.AddUserToRoleRequest;
import com.larasierra.movietickets.user.model.DefaultAppUserResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RestController
@Validated
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/self")
    public DefaultAppUserResponse findSelf() {
        return appUserService.findSelf();
    }

    @PostMapping("/{id}/role")
    public void addUserToRole(
            @ValidId @PathVariable("id") String userId,
            @Valid @RequestBody AddUserToRoleRequest request
    ) {
        appUserService.addUserToRole(userId, request);
    }

    @DeleteMapping("/{id}/role/{roleId}")
    public ResponseEntity<Void> removeUserFromRole(
            @ValidId @PathVariable("id") String userId,
            @NotNull @PathVariable("roleId") String roleId
    ) {
        appUserService.removeUserFromRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}

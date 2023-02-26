package com.larasierra.movietickets.user.application;

import com.larasierra.movietickets.shared.exception.AppBadRequestException;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.util.AuthInfo;
import com.larasierra.movietickets.shared.util.IdUtil;
import com.larasierra.movietickets.user.domain.AppRole;
import com.larasierra.movietickets.user.domain.AppUser;
import com.larasierra.movietickets.user.external.cognito.AppUserCognitoRepository;
import com.larasierra.movietickets.user.external.dynamodb.AppRoleDynamoDbRepository;
import com.larasierra.movietickets.user.external.dynamodb.AppUserDynamoDbRepository;
import com.larasierra.movietickets.user.model.AddUserToRoleRequest;
import com.larasierra.movietickets.user.model.DefaultAppUserResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AppUserService {

    private final AppUserDynamoDbRepository appUserDynamoDbRepository;
    private final AppRoleDynamoDbRepository appRoleDynamoDbRepository;
    private final AppUserCognitoRepository appUserCognitoRepository;
    private final AuthInfo authInfo;

    public AppUserService(AppUserDynamoDbRepository appUserDynamoDbRepository, AppRoleDynamoDbRepository appRoleDynamoDbRepository, AppUserCognitoRepository appUserCognitoRepository, AuthInfo authInfo) {
        this.appUserDynamoDbRepository = appUserDynamoDbRepository;
        this.appRoleDynamoDbRepository = appRoleDynamoDbRepository;
        this.appUserCognitoRepository = appUserCognitoRepository;
        this.authInfo = authInfo;
    }

    @PreAuthorize("hasRole('internal')")
    public void create() {
        var user = new AppUser(IdUtil.next(), "email", "cognitoId", OffsetDateTime.now(), null);
        appUserDynamoDbRepository.create(user);
    }

    @PreAuthorize("isAuthenticated()")
    public DefaultAppUserResponse findSelf() {
        AppUser user = appUserDynamoDbRepository.findById(authInfo.userId())
                .orElseThrow(AppResourceNotFoundException::new);

        return new DefaultAppUserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getRoles(),
                user.getCreatedAt()
        );
    }

    @PreAuthorize("hasRole('internal')")
    public void addUserToRole(String userId, AddUserToRoleRequest request) {
        // assert that the role exists
        AppRole role = appRoleDynamoDbRepository.findById(request.roleId())
                .orElseThrow(() -> new AppBadRequestException("invalid role"));

        AppUser user = appUserDynamoDbRepository.findById(userId)
                .orElseThrow(AppResourceNotFoundException::new);

        // skip calling external services if user is already in the role
        boolean isUserAlreadyInRole = user.hasRole(role.getRoleId());

        if (isUserAlreadyInRole) {
            return;
        }

        appUserDynamoDbRepository.addUserToGroup(userId, role.getRoleId());

        appUserCognitoRepository.addUserToGroup(user.getCognitoId(), role.getRoleId());
    }

    @PreAuthorize("hasRole('internal')")
    public void removeUserFromRole(String userId, String roleId) {
        // assert that the role exists
        AppRole role = appRoleDynamoDbRepository.findById(roleId)
                .orElseThrow(() -> new AppBadRequestException("invalid role"));

        AppUser user = appUserDynamoDbRepository.findById(userId)
                .orElseThrow(AppResourceNotFoundException::new);

        // skip calling external services if user is not in role
        boolean isNotUserInRole = !user.hasRole(role.getRoleId());

        if (isNotUserInRole) {
            return;
        }

        appUserCognitoRepository.removeUserFromGroup(user.getCognitoId(), role.getRoleId());

        appUserDynamoDbRepository.removeUserFromGroup(userId, role.getRoleId());
    }
}

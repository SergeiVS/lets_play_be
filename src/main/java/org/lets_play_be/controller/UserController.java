package org.lets_play_be.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.UserControllerApi;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.UserAvailabilityUpdateRequest;
import org.lets_play_be.dto.userDto.UserDataUpdateRequest;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

    private final AppUserService appUserService;

    @Override
    public ResponseEntity<AppUserFullResponse> getUserData(Principal principal) {
        AppUserFullResponse response = appUserService.getAppUserFullData(principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AppUserFullResponse> updateUserData(UserDataUpdateRequest request, Principal principal) {
        AppUserFullResponse response = appUserService.updateUserNameAndAvatarUrl(request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AppUserFullResponse> updateUserAvailability(UserAvailabilityUpdateRequest request, Principal principal) {
        AppUserFullResponse response = appUserService.updateUserAvailability(request, principal.getName());
        return ResponseEntity.ok(response);
    }
}

package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.UserControllerApi;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.UserAvailabilityUpdateRequest;
import org.lets_play_be.dto.userDto.UserDataUpdateRequest;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerApi {

    private final AppUserService appUserService;

    @Override
    public ResponseEntity<List<AppUserFullResponse>> getAllUsers() {
        List<AppUserFullResponse> response = appUserService.getAllUsers();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AppUserFullResponse> getUserData(Principal principal) {
        AppUserFullResponse response = appUserService.getAppUserFullData(principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AppUserFullResponse> updateUserData(UserDataUpdateRequest request, Principal principal) {
        AppUserFullResponse response = appUserService.updateUserData(request, principal.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AppUserFullResponse> updateUserAvailability(UserAvailabilityUpdateRequest request, Principal principal) {
        AppUserFullResponse response = appUserService.updateUserAvailability(request, principal.getName());
        return ResponseEntity.ok(response);
    }
}

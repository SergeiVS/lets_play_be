package org.lets_play_be.controller.api;

import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.UserAvailabilityUpdateRequest;
import org.lets_play_be.dto.userDto.UserDataUpdateRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/user")
public interface UserControllerApi {
    @GetMapping
    AppUserFullResponse getUserData(Principal principal);

    @PutMapping
    AppUserFullResponse updateUserData(UserDataUpdateRequest request, Principal principal);

    @PutMapping("availability")
    AppUserFullResponse updateUserAvailability(UserAvailabilityUpdateRequest request, Principal principal);
}

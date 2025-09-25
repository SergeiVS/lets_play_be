package org.lets_play_be.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lets_play_be.common.UrlEnum;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.security.utils.UserDetailsMapper;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.appUserService.RegisterNewUserService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String GITHUB_REGISTRATION = "github";
    private static final String DISCORD_REGISTRATION = "discord";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String GITHUB_USERNAME_ATTRIBUTE = "login";
    private static final String DISCORD_USERNAME_ATTRIBUTE = "username";

    private final AppUserService userService;
    private final RegisterNewUserService newUserService;
    private final RestClient restClient;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User loadedUser = super.loadUser(userRequest);
        var registrationId = userRequest.getClientRegistration().getRegistrationId();
        assert registrationId != null;

        log.info(loadedUser.getAttributes().toString());

        String email = getEmail(userRequest, loadedUser, registrationId);
        String avatarUrl = getAvatarUrl(loadedUser, registrationId);
        String username = getUserName(loadedUser, registrationId);
        Optional<AppUser> userOpt = userService.getOptionalUserByEmail(email);

        OAuth2User auth2UserForAuthentication = getOAuth2UserOrElseRegisterNewUser(userOpt, username, email, avatarUrl);
        setAuthentication(userRequest, auth2UserForAuthentication);

        return auth2UserForAuthentication;
    }

    private void setAuthentication(OAuth2UserRequest userRequest, OAuth2User auth2UserForAuthentication) {
        Authentication auth = new OAuth2AuthenticationToken(
                auth2UserForAuthentication,
                auth2UserForAuthentication.getAuthorities(),
                userRequest.getClientRegistration().getRegistrationId()
        );

        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private OAuth2User getOAuth2UserOrElseRegisterNewUser(
            Optional<AppUser> userOpt,
            String username,
            String email,
            String avatarUrl
    ) {
        if (userOpt.isPresent()) {
            return new UserDetailsMapper(userOpt.get());
        } else {
            var newUser = newUserService.registerNewOAuth2User(
                    username,
                    email,
                    avatarUrl
            );
            return new UserDetailsMapper(newUser);
        }
    }

    private String getUserName(OAuth2User auth2User, String registrationId) {
        if (registrationId.equals(GITHUB_REGISTRATION)) {
            return auth2User.getAttribute(GITHUB_USERNAME_ATTRIBUTE);
        } else if (registrationId.equals(DISCORD_REGISTRATION)) {
            return auth2User.getAttribute(DISCORD_USERNAME_ATTRIBUTE);
        } else {
            throw new OAuth2AuthenticationException(new OAuth2Error(String.format("No registration %s found", registrationId)));
        }
    }

    private String getEmail(OAuth2UserRequest userRequest, OAuth2User user, String registrationId) {
        String email = user.getAttribute("email");
        if (email == null) {
            if (registrationId.equals(GITHUB_REGISTRATION)) {
                email = fetchGitHubPrimaryEmailAddress(userRequest.getAccessToken().getTokenValue());
            } else if (registrationId.equals(DISCORD_REGISTRATION)) {
                email = fetchDiscordEmailAddress(userRequest.getAccessToken().getTokenValue());
            } else {
                throw new OAuth2AuthenticationException(new OAuth2Error(String.format("No registration %s found", registrationId)));
            }
        }
        assert email != null;
        return email;
    }

    private String fetchDiscordEmailAddress(String tokenValue) {
        MultiValueMap<String, String> body = restClient
                .get()
                .uri(UrlEnum.DISCORD_USERINFO_URL.getUrl())
                .header(HttpHeaders.AUTHORIZATION,
                        BEARER_PREFIX + tokenValue)
                .header(HttpHeaders.ACCEPT, "application/x-www-form-urlencoded")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (body == null || body.isEmpty()) {
            return null;
        }
        return body.getOrDefault("email", new ArrayList<>()).getFirst();
    }

    private String fetchGitHubPrimaryEmailAddress(String token) {
        List<EmailVm> emailVmList = restClient
                .get()
                .uri(UrlEnum.GITHUB_EMAILS_URL.getUrl())
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        if (emailVmList == null || emailVmList.isEmpty()) {
            return null;
        }
        return emailVmList.stream()
                .filter(EmailVm::primary)
                .findFirst()
                .map(EmailVm::email)
                .orElse(null);
    }

    private record EmailVm(String email, Boolean primary) {
    }

    private String getAvatarUrl(OAuth2User loadedUser, String registrationId) {
        if (registrationId.equals(GITHUB_REGISTRATION)) {
            return loadedUser.getAttribute("avatar_url");
        } else if (registrationId.equals(DISCORD_REGISTRATION)) {
            return String.format(
                    UrlEnum.DISCORD_AVATAR_URL.getUrl(),
                    loadedUser.getAttribute("id"),
                    loadedUser.getAttribute("avatar"));
        } else {
            return null;
        }
    }
}

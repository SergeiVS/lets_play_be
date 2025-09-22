package org.lets_play_be.security.oauth2;

import lombok.RequiredArgsConstructor;
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
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AppUserService userService;
    private final RegisterNewUserService newUserService;
    private final RestClient restClient;
    private static final String EMAILS_URL = "https://api.github.com/user/emails";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User loadedUser = super.loadUser(userRequest);

        String email = getEmail(userRequest, loadedUser);
        Optional<AppUser> userOpt = userService.getOptionalUserByEmail(email);
        OAuth2User auth2UserForAuthentication = getOAuth2UserOrElseRegisterNewUser(userOpt, loadedUser, email);
        setAutentication(userRequest, auth2UserForAuthentication);

        return auth2UserForAuthentication;
    }

    private static void setAutentication(OAuth2UserRequest userRequest, OAuth2User auth2UserForAuthentication) {
        Authentication auth = new OAuth2AuthenticationToken(auth2UserForAuthentication, auth2UserForAuthentication.getAuthorities(), userRequest.getClientRegistration().getRegistrationId());
        auth.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private OAuth2User getOAuth2UserOrElseRegisterNewUser(
            Optional<AppUser> userOpt,
            OAuth2User loadedUser,
            String email
    ) {
        OAuth2User auth2User;
        if (userOpt.isPresent()) {
            auth2User = new UserDetailsMapper(userOpt.get());
        } else {
            var newUser = newUserService.registerNewOAuth2User(
                    loadedUser.getName(),
                    email,
                    loadedUser.getAttribute("avatar_url")
            );
            auth2User = new UserDetailsMapper(newUser);
        }
        return auth2User;
    }

    private String getEmail(OAuth2UserRequest userRequest, OAuth2User user) {
        String email = user.getAttribute("email");
        if (email == null) {
            email = fetchPrimaryEmailAddress(userRequest.getAccessToken().getTokenValue());
        }
        return email;
    }

    private String fetchPrimaryEmailAddress(String token) {
        List<EmailVm> emailVmList = restClient
                .get()
                .uri(EMAILS_URL)
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
}

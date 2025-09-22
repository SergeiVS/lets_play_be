package org.lets_play_be.security.utils;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class UserDetailsMapper implements UserDetails, OAuth2User {

    public static final String NAME_ATTRIBUTE = "name";
    public static final String EMAIL_ATTRIBUTE = "email";
    public static final String AVATAR_URL = "avatar_url";
    private final AppUser user;

    @Override
    public <A> A getAttribute(String name) {
        if (name.equals(NAME_ATTRIBUTE) || name.equals(EMAIL_ATTRIBUTE) || name.equals(AVATAR_URL)) {
            return OAuth2User.super.getAttribute(name);
        }
        throw new IllegalArgumentException("Attribute " + name + " not supported");
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(NAME_ATTRIBUTE, user.getName());
        attributes.put(EMAIL_ATTRIBUTE, user.getEmail());

        if (user.getAvatarUrl() != null) {
            attributes.put(AVATAR_URL, user.getAvatarUrl());
        } else {
            attributes.put(AVATAR_URL, "N/A");
        }
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "";
    }
}

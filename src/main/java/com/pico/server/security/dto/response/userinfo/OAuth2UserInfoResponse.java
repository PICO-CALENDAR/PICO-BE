package com.pico.server.security.dto.response.userinfo;

public interface OAuth2UserInfoResponse {

    String getProvider();

    String getProviderId();

    String getEmail();

    String getName();

    String getProfileImage();
}

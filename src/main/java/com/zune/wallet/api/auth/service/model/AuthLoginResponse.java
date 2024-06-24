package com.zune.wallet.api.auth.service.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthLoginResponse {
    private final Long id;
    private final String accessToken;

    public static AuthLoginResponse build(Long id, String accessToken) {
        return AuthLoginResponse.builder()
                .id(id)
                .accessToken(accessToken)
                .build();
    }
}

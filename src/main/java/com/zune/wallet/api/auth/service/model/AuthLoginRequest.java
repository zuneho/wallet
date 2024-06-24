package com.zune.wallet.api.auth.service.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthLoginRequest {

    @NotEmpty(message = "올바른 이메일을 입력해주세요.")
    private final String email;

    @NotEmpty(message = "올바른 비밀번호를 입력해주세요.")
    private final String password;

    @Builder
    public AuthLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

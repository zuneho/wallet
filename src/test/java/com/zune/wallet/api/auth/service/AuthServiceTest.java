package com.zune.wallet.api.auth.service;

import com.zune.wallet.TestBase;
import com.zune.wallet.api.auth.service.model.AuthLoginRequest;
import com.zune.wallet.api.auth.service.model.AuthLoginResponse;
import com.zune.wallet.api.common.exception.AuthException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


class AuthServiceTest extends TestBase {

    @Autowired
    private AuthService authService;

    @Test
    public void test_ok_회원_로그인() {
        AuthLoginRequest request = AuthLoginRequest.builder()
                .email("test@test.com")
                .password("1234")
                .build();

        AuthLoginResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAccessToken()).isNotEmpty();
    }

    @Test
    public void test_error_비밀번호_오류_로그인_불가() {
        AuthLoginRequest request = AuthLoginRequest.builder()
                .email("test@test.com")
                .password("12345")
                .build();

        Exception exception = Assertions.assertThrows(AuthException.class, () -> authService.login(request));
        String expectedMessage = "비밀번호가 일치하지 않습니다.";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }
}
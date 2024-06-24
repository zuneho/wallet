package com.zune.wallet.api.auth.controller;

import com.zune.wallet.api.auth.service.AuthService;
import com.zune.wallet.api.auth.service.model.AuthLoginRequest;
import com.zune.wallet.api.auth.service.model.AuthLoginResponse;
import com.zune.wallet.api.common.model.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<BaseResponse> login(@RequestBody @Valid AuthLoginRequest request) {
        AuthLoginResponse response = authService.login(request);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }
}

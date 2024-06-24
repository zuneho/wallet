package com.zune.wallet.api.member.controller;

import com.zune.wallet.api.common.model.BaseResponse;
import com.zune.wallet.api.member.service.MemberService;
import com.zune.wallet.api.member.service.model.MemberCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/api/v1/member")
    public ResponseEntity<BaseResponse> create(@RequestBody @Valid MemberCreateRequest request) {
        memberService.create(request);
        return ResponseEntity.ok(BaseResponse.ok("회원 가입이 완료 되었습니다."));
    }
}

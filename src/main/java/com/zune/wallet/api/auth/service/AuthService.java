package com.zune.wallet.api.auth.service;

import com.zune.wallet.api.auth.service.model.AuthLoginRequest;
import com.zune.wallet.api.auth.service.model.AuthLoginResponse;
import com.zune.wallet.api.auth.service.model.MemberDetailDto;
import com.zune.wallet.api.common.exception.AuthException;
import com.zune.wallet.domain.common.util.JwtUtil;
import com.zune.wallet.domain.persistance.member.Member;
import com.zune.wallet.domain.persistance.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public AuthLoginResponse login(AuthLoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("올바른 사용자 정보를 확인할 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new AuthException("비밀번호가 일치하지 않습니다.");
        }
        String accessToken = jwtUtil.createAccessToken(MemberDetailDto.of(member));
        return AuthLoginResponse.build(member.getId(), accessToken);
    }
}

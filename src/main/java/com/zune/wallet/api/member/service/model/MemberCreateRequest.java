package com.zune.wallet.api.member.service.model;

import com.zune.wallet.domain.persistance.member.Member;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class MemberCreateRequest {
    @NotEmpty(message = "이메일을 입력해주세요")
    private final String email;
    @NotEmpty(message = "이름을 입력해주세요")
    private final String name;
    @NotEmpty(message = "비밀번호를 입력해주세요")
    private final String password;
    @NotEmpty(message = "출금 계좌를 입력해주세요")
    private final String withdrawalAccount;

    @Builder
    public MemberCreateRequest(String email, String name, String password, String withdrawalAccount) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.withdrawalAccount = withdrawalAccount;
    }

    public Member toEntity(String depositAccount, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(this.email)
                .name(this.name)
                .password(passwordEncoder.encode(this.password))
                .depositAccount(depositAccount)
                .withdrawalAccount(this.withdrawalAccount)
                .build();
    }
}

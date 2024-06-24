package com.zune.wallet.api.auth.service.model;

import com.zune.wallet.domain.persistance.member.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
public class MemberDetailDto implements UserDetails {

    private Long id;
    private String email;
    private String name;
    private String password;
    private String depositAccount;
    private String withdrawalAccount;

    public static MemberDetailDto of(Member member) {
        return MemberDetailDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .password(member.getPassword())
                .depositAccount(member.getDepositAccount())
                .withdrawalAccount(member.getWithdrawalAccount())
                .build();
    }


    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.name;
    }
}

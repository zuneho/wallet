package com.zune.wallet.api.auth.service;

import com.zune.wallet.api.auth.service.model.MemberDetailDto;
import com.zune.wallet.domain.persistance.member.Member;
import com.zune.wallet.domain.persistance.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public MemberDetailDto loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found. memberId :" + memberId));
        return MemberDetailDto.of(member);
    }
}

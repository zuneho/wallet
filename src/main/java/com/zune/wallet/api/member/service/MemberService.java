package com.zune.wallet.api.member.service;

import com.zune.wallet.api.member.service.model.MemberCreateRequest;
import com.zune.wallet.domain.persistance.bank.ExternalBank;
import com.zune.wallet.domain.persistance.member.Member;
import com.zune.wallet.domain.persistance.member.MemberRepository;
import com.zune.wallet.domain.persistance.wallet.Wallet;
import com.zune.wallet.domain.persistance.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExternalBank externalBank;

    @Transactional
    public Long create(MemberCreateRequest request) {
        Optional<Member> equalEmailUser = memberRepository.findByEmail(request.getEmail());
        if (equalEmailUser.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 이메일 입니다.");
        }
        String virtualAccount = externalBank.createVirtualAccount();
        Member newMember = request.toEntity(virtualAccount, passwordEncoder);
        memberRepository.save(newMember);

        Long memberId = newMember.getId();
        Wallet newWallet = new Wallet(memberId);
        walletRepository.save(newWallet);

        return newMember.getId();
    }
}

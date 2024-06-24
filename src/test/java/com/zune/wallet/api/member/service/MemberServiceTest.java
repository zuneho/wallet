package com.zune.wallet.api.member.service;

import com.zune.wallet.TestBase;
import com.zune.wallet.api.member.service.model.MemberCreateRequest;
import com.zune.wallet.domain.persistance.member.Member;
import com.zune.wallet.domain.persistance.member.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class MemberServiceTest extends TestBase {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void test_ok_회원가입() {
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("test2@test.com")
                .name("test2")
                .password("1234")
                .withdrawalAccount("123456789")
                .build();

        Long memberId = memberService.create(request);
        assertThat(memberId).isNotNull();

        //생성된 회원에 대한 검증
        Member member = memberRepository.findById(memberId)
                .orElse(null);

        assertThat(member).isNotNull();
        assertThat(member.getDepositAccount()).isNotEmpty(); //가상 계좌 자동 할당

        assertThat(member.getPassword()).isNotEmpty(); // password Encoder 처리
        assertThat(member.getPassword()).isNotEqualTo("1234");
    }

    @Test
    public void test_error_중복된_이메일_가입() {
        MemberCreateRequest request = MemberCreateRequest.builder()
                .email("test@test.com")
                .name("test2")
                .password("1234")
                .withdrawalAccount("123456789")
                .build();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> memberService.create(request));

        String expectedMessage = "이미 등록된 이메일 입니다.";
        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }
}
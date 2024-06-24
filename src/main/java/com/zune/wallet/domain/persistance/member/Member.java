package com.zune.wallet.domain.persistance.member;

import com.zune.wallet.domain.common.model.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "member", schema = "wallet")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(name = "deposit_account", nullable = false)
    private String depositAccount;

    @Column(name = "withdrawal_account", nullable = false)
    private String withdrawalAccount;

    @Builder
    public Member(String email, String name, String password, String depositAccount, String withdrawalAccount) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.depositAccount = depositAccount;
        this.withdrawalAccount = withdrawalAccount;
    }
}

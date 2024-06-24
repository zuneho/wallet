package com.zune.wallet.domain.persistance.wallet;

import com.zune.wallet.domain.common.model.BaseTimeEntity;
import com.zune.wallet.domain.persistance.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "wallet", schema = "wallet")
public class Wallet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private double balance;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
    private List<WalletTransaction> walletTransactions;


    @Builder
    public Wallet(Long memberId) {
        this.memberId = memberId;
        this.balance = 0.0d;
    }

    public void setBalance(double amount) {
        this.balance = amount;
    }
}

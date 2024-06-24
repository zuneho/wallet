package com.zune.wallet.domain.persistance.wallet;

import com.zune.wallet.domain.persistance.member.Member;
import com.zune.wallet.domain.persistance.wallet.type.WalletTxType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "wallet_transaction", schema = "wallet")
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false, length = 50)
    private WalletTxType type;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "purchase_id")
    private Long purchaseId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, insertable = false, updatable = false)
    private Wallet wallet;

    @Builder
    public WalletTransaction(Long memberId, WalletTxType type, double amount, Long purchaseId) {
        this.memberId = memberId;
        this.type = type;
        this.amount = amount;
        this.purchaseId = purchaseId;
    }
}

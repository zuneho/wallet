package com.zune.wallet.domain.persistance.purchase;

import com.zune.wallet.api.common.exception.BusinessException;
import com.zune.wallet.domain.persistance.member.Member;
import com.zune.wallet.domain.persistance.product.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "purchase", schema = "wallet")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "purchase_amount", nullable = false)
    private Double purchaseAmount;

    @CreationTimestamp
    @Column(name = "purchase_at", nullable = false)
    private LocalDateTime purchaseAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "is_canceled")
    private boolean canceled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @Builder
    public Purchase(Long memberId, Long productId, Double purchaseAmount) {
        this.memberId = memberId;
        this.productId = productId;
        this.purchaseAmount = purchaseAmount;
        this.canceled = false;
    }

    public void cancelPurchase() {
        if (this.canceled) {
            throw new BusinessException("이미 취소 처리된 주문 입니다.");
        }
        this.canceled = true;
        this.canceledAt = LocalDateTime.now();
    }
}

package com.zune.wallet.domain.persistance.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p LEFT OUTER JOIN FETCH p.product WHERE p.id = :purchaseId AND p.memberId = :memberId")
    Purchase findByIdAndMemberId(@Param("purchaseId") Long purchaseId, @Param("memberId") Long memberId);
}

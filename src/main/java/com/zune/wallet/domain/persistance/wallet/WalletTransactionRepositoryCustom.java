package com.zune.wallet.domain.persistance.wallet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WalletTransactionRepositoryCustom {
    Page<WalletTransaction> findAllByMemberId(Long memberId, Pageable pageable);

    double sumDailyWithdrawnPositiveAmount(Long memberId);
}

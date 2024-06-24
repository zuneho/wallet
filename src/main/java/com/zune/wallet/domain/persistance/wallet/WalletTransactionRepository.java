package com.zune.wallet.domain.persistance.wallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long>, WalletTransactionRepositoryCustom {
}

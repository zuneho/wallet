package com.zune.wallet.domain.persistance.wallet;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.zune.wallet.domain.persistance.wallet.type.WalletTxType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static com.zune.wallet.domain.persistance.wallet.QWalletTransaction.walletTransaction;

public class WalletTransactionRepositoryImpl extends QuerydslRepositorySupport implements WalletTransactionRepositoryCustom {
    public WalletTransactionRepositoryImpl() {
        super(WalletTransactionRepository.class);
    }

    @Override
    public Page<WalletTransaction> findAllByMemberId(Long memberId, Pageable pageable) {
        if (memberId == null) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        JPQLQuery<WalletTransaction> query = from(walletTransaction)
                .where(walletTransaction.memberId.eq(memberId))
                .orderBy(walletTransaction.id.desc());

        List<WalletTransaction> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, query::fetchCount);
    }

    @Override
    public double sumDailyWithdrawnPositiveAmount(Long memberId) {
        LocalDate today = LocalDate.now();

        BooleanBuilder where = new BooleanBuilder()
                .and(walletTransaction.memberId.eq(memberId))
                .and(walletTransaction.createdAt.goe(today.atTime(LocalTime.MIN)))
                .and(walletTransaction.createdAt.loe(today.atTime(23, 59, 59, 0)))
                .and(walletTransaction.type.eq(WalletTxType.WITHDRAW));

        Double sumAmount = from(walletTransaction)
                .select(walletTransaction.amount.sum())
                .where(where)
                .fetchOne();

        return sumAmount != null ? Math.abs(sumAmount) : 0.0D;
    }
}

package com.zune.wallet.api.wallet.service;

import com.zune.wallet.api.common.exception.BusinessException;
import com.zune.wallet.api.common.exception.CodeException;
import com.zune.wallet.api.wallet.service.model.WalletBalanceResponse;
import com.zune.wallet.api.wallet.service.model.WalletHistoryResponse;
import com.zune.wallet.domain.common.util.NumberUtil;
import com.zune.wallet.domain.persistance.bank.ExternalBank;
import com.zune.wallet.domain.persistance.wallet.Wallet;
import com.zune.wallet.domain.persistance.wallet.WalletRepository;
import com.zune.wallet.domain.persistance.wallet.WalletTransaction;
import com.zune.wallet.domain.persistance.wallet.WalletTransactionRepository;
import com.zune.wallet.domain.persistance.wallet.type.WalletTxType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletService {
    private static final double DAILY_MAX_WITHDRAW_AMOUNT = 30_000_000.0D;
    private static final double PER_MAX_WITHDRAW_AMOUNT = 10_000_000.0D;


    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final ExternalBank externalBankRepository;

    @Transactional(readOnly = true)
    public WalletBalanceResponse getBalance(Long memberId) {
        Wallet wallet = walletRepository.findByMemberId(memberId);
        if (wallet == null) {
            log.error("not found user wallet");
            throw new CodeException("월렛 정보를 불러올 수 없습니다.");
        }
        return WalletBalanceResponse.build(memberId, wallet.getBalance());
    }

    @Transactional
    public WalletBalanceResponse deposit(WalletTxType walletTxType, Long memberId, double requestAmount) {
        return deposit(walletTxType, memberId, requestAmount, null);
    }

    @Transactional
    public WalletBalanceResponse deposit(WalletTxType walletTxType, Long memberId, double requestAmount, Long purchaseId) {
        if (walletTxType == null || !walletTxType.isDepositType()) {
            log.error("deposit error. walletEventType={} memberId={} requestAmount={} ", memberId, requestAmount, walletTxType);
            throw new IllegalArgumentException("올바른 요청이 아닙니다.");
        }

        Wallet wallet = walletRepository.findByMemberId(memberId);
        if (wallet == null) {
            log.error("wallet not found. memberId={}", memberId);
            throw new IllegalArgumentException("올바른 월렛 정보를 확인 할 수 없습니다.");
        }

        double positiveAmount = Math.abs(requestAmount);
        double balance = wallet.getBalance() + positiveAmount;

        wallet.setBalance(balance);
        walletRepository.save(wallet);

        WalletTransaction newTransaction = WalletTransaction.builder()
                .memberId(memberId)
                .amount(positiveAmount)
                .type(walletTxType)
                .purchaseId(purchaseId)
                .build();
        walletTransactionRepository.save(newTransaction);

        return WalletBalanceResponse.build(wallet.getMemberId(), wallet.getBalance());
    }

    @Transactional
    public WalletBalanceResponse withdraw(WalletTxType walletTxType, Long memberId, double requestAmount) {
        return withdraw(walletTxType, memberId, requestAmount, null);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public WalletBalanceResponse withdraw(WalletTxType walletTxType, Long memberId, double requestAmount, Long purchaseId) {
        if (walletTxType == null || !walletTxType.isWithdrawType()) {
            log.error("withdraw error. walletEventType={} memberId={} requestAmount={} ", memberId, requestAmount, walletTxType);
            throw new IllegalArgumentException("올바른 요청이 아닙니다.");
        }

        Wallet wallet = walletRepository.findByMemberId(memberId);
        if (wallet == null) {
            log.error("wallet not found. memberId={}", memberId);
            throw new IllegalArgumentException("올바른 월렛 정보를 확인 할 수 없습니다.");
        }
        double negativeAmount = -Math.abs(requestAmount);
        double positiveAmount = Math.abs(requestAmount);

        if (wallet.getBalance() + negativeAmount < 0) { //출금 가능한 잔고 확인
            throw new BusinessException(walletTxType.getDescription() + " 가능한 잔액이 부족 합니다.");
        }

        if (WalletTxType.WITHDRAW == walletTxType) { //순수 출금 요청일 경우에만 체크하는 항목
            if (positiveAmount > PER_MAX_WITHDRAW_AMOUNT) { //1회 출금 한도 체크
                throw new BusinessException(String.format("%s 요청 시 1회 최대 한도는 %s원 입니다.", walletTxType.getDescription(), NumberUtil.doubleToFormatedString(PER_MAX_WITHDRAW_AMOUNT)));
            }

            double dailyWithdrawnAmount = walletTransactionRepository.sumDailyWithdrawnPositiveAmount(memberId);
            if (dailyWithdrawnAmount + positiveAmount > DAILY_MAX_WITHDRAW_AMOUNT) { //1일 출금 한도 체크
                throw new BusinessException(String.format("%s 요청 시 1일 최대 한도는 %s원 입니다.", walletTxType.getDescription(), NumberUtil.doubleToFormatedString(DAILY_MAX_WITHDRAW_AMOUNT)));
            }

            String bankApiErrorMessage = externalBankRepository.withdraw(positiveAmount); //일반 출금 처리 시 은행 api 를 통한 출금 요청
            if (StringUtils.isNotEmpty(bankApiErrorMessage)) {
                throw new BusinessException("은행 출금 처리가 불가능합니다." + bankApiErrorMessage);
            }
        }

        double balance = wallet.getBalance() + negativeAmount;
        wallet.setBalance(balance);
        walletRepository.save(wallet);

        WalletTransaction newTransaction = WalletTransaction.builder()
                .memberId(memberId)
                .amount(negativeAmount)
                .type(walletTxType)
                .purchaseId(purchaseId)
                .build();
        walletTransactionRepository.save(newTransaction);

        return WalletBalanceResponse.build(wallet.getMemberId(), wallet.getBalance());
    }

    @Transactional(readOnly = true)
    public Page<WalletHistoryResponse> getTransactionHistory(Long memberId, Pageable pageable) {
        return walletTransactionRepository.findAllByMemberId(memberId, pageable)
                .map(WalletHistoryResponse::of);
    }
}

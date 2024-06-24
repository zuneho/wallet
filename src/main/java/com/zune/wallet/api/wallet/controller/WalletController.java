package com.zune.wallet.api.wallet.controller;

import com.zune.wallet.api.auth.service.model.MemberDetailDto;
import com.zune.wallet.api.common.model.BaseResponse;
import com.zune.wallet.api.wallet.service.WalletService;
import com.zune.wallet.api.wallet.service.model.WalletBalanceResponse;
import com.zune.wallet.api.wallet.service.model.WalletHistoryResponse;
import com.zune.wallet.api.wallet.service.model.WalletTxRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallet")
@RestController
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> getBalance(@AuthenticationPrincipal MemberDetailDto member) {
        WalletBalanceResponse walletBalanceResponse = walletService.getBalance(member.getId());
        return ResponseEntity.ok(BaseResponse.ok(walletBalanceResponse));
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> getTransactionHistory(
            @AuthenticationPrincipal MemberDetailDto member,
            @PageableDefault(size = 20, sort = {"id"}, direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        Page<WalletHistoryResponse> response = walletService.getTransactionHistory(member.getId(), pageable);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @PostMapping("/deposit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> deposit(
            @AuthenticationPrincipal MemberDetailDto member,
            @RequestBody @Valid WalletTxRequest request
    ) {
        if (!request.isDeposit()) {
            throw new IllegalArgumentException("올바른 요청이 아닙니다.");
        }
        WalletBalanceResponse walletBalanceResponse = walletService.deposit(request.getEventType(), member.getId(), request.getAmount());
        return ResponseEntity.ok(BaseResponse.ok(walletBalanceResponse));
    }

    @PutMapping("/withdraw")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> withdraw(
            @AuthenticationPrincipal MemberDetailDto member,
            @RequestBody @Valid WalletTxRequest request
    ) {
        if (!request.isWithdraw()) {
            throw new IllegalArgumentException("올바른 요청이 아닙니다.");
        }
        WalletBalanceResponse walletBalanceResponse = walletService.withdraw(request.getEventType(), member.getId(), request.getAmount());
        return ResponseEntity.ok(BaseResponse.ok(walletBalanceResponse));
    }
}

package com.zune.wallet.api.purchase.controller;

import com.zune.wallet.api.auth.service.model.MemberDetailDto;
import com.zune.wallet.api.common.model.BaseResponse;
import com.zune.wallet.api.purchase.service.PurchaseService;
import com.zune.wallet.api.purchase.service.model.PurchaseRequest;
import com.zune.wallet.api.purchase.service.model.PurchaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase")
@RestController
public class PurchaseController {
    private final PurchaseService purchaseService;

    @GetMapping("/{purchaseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> getPurchase(
            @AuthenticationPrincipal MemberDetailDto member,
            @PathVariable Long purchaseId
    ) {
        PurchaseResponse response = purchaseService.getPurchase(member.getId(), purchaseId);
        return ResponseEntity.ok(BaseResponse.ok(response));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> purchaseProduct(
            @AuthenticationPrincipal MemberDetailDto member,
            @RequestBody @Valid PurchaseRequest request
    ) {
        Long purchaseId = purchaseService.purchaseProduct(member.getId(), request);
        return ResponseEntity.ok(BaseResponse.ok(Map.of("purchaseId", purchaseId)));
    }


    @DeleteMapping("/{purchaseId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse> cancelPurchase(
            @AuthenticationPrincipal MemberDetailDto member,
            @PathVariable Long purchaseId
    ) {
        purchaseService.cancelPurchase(member.getId(), purchaseId);
        return ResponseEntity.ok(BaseResponse.ok("취소 되었습니다."));
    }


}

package com.zune.wallet.domain.persistance.bank;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MockExternalBankRepository implements ExternalBank {

    @Override //TODO 외부 은행과 연동 하여 출금 처리가 필요함.
    @Transactional(propagation = Propagation.MANDATORY)
    public String withdraw(double positiveAmount) {
        return null;
    }

    @Override //TODO 외부 은행과 연동 하여 가상 계좌를 생성하는 로직이 필요함.
    @Transactional(propagation = Propagation.MANDATORY)
    public String createVirtualAccount() {
        return UUID.randomUUID().toString();
    }
}

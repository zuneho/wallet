package com.zune.wallet.domain.persistance.bank;

public interface ExternalBank {

    String withdraw(double positiveAmount);

    String createVirtualAccount();

}

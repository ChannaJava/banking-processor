package com.banking.processor.domain;

import com.banking.processor.exception.InsufficientFundsException;
import com.banking.processor.exception.InvalidAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private UUID accountId;
    private Account account;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        account = new Account(accountId, Money.of(100.00));
    }

    @Test
    @DisplayName("Should initialize account with initial balance and log opening transaction")
    void initializeAccount_Success() {
        assertEquals(Money.of(100.00), account.getBalance());
        assertEquals(1, account.getLedger().size());
        assertEquals(TransactionType.DEPOSIT, account.getLedger().get(0).getType());
    }

    @Test
    @DisplayName("Should successfully deposit positive amount")
    void deposit_PositiveAmount_UpdatesBalanceAndLedger() {
        account.deposit(Money.of(50.00));
        assertEquals(Money.of(150.00), account.getBalance());
        assertEquals(2, account.getLedger().size());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.00, -10.00, -0.01})
    @DisplayName("Should throw exception when depositing non-positive amount")
    void deposit_InvalidAmount_ThrowsException(double amount) {
        assertThrows(InvalidAmountException.class, () -> account.deposit(Money.of(amount)));
    }

    @Test
    @DisplayName("Should successfully withdraw available funds")
    void withdraw_SufficientBalance_UpdatesBalanceAndLedger() {
        account.withdraw(Money.of(40.00));
        assertEquals(Money.of(60.00), account.getBalance());
        assertEquals(2, account.getLedger().size());
    }

    @Test
    @DisplayName("Should prevent withdrawal causing overdraft")
    void withdraw_InsufficientFunds_ThrowsException() {
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(Money.of(100.01)));
        assertEquals(Money.of(100.00), account.getBalance()); // Unchanged balance
    }

    @Test
    @DisplayName("Should correctly adjust balances on outbound and inbound transfers")
    void transfer_ValidAmount_UpdatesLedgers() {
        UUID targetId = UUID.randomUUID();
        Account target = new Account(targetId, Money.of(10.00));

        account.sendTransfer(Money.of(30.00), targetId);
        target.receiveTransfer(Money.of(30.00), accountId);

        assertEquals(Money.of(70.00), account.getBalance());
        assertEquals(Money.of(40.00), target.getBalance());

        assertEquals(TransactionType.TRANSFER_OUT, account.getLedger().get(1).getType());
        assertEquals(TransactionType.TRANSFER_IN, target.getLedger().get(1).getType());
    }
}
package com.banking.processor.service;

import com.banking.processor.domain.Account;
import com.banking.processor.domain.Money;
import com.banking.processor.exception.AccountNotFoundException;
import com.banking.processor.exception.InvalidAmountException;
import com.banking.processor.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Should execute atomic transfers between accounts")
    void transfer_SuccessfulExecution() {
        Account acc1 = accountService.createAccount(BigDecimal.valueOf(500.00));
        Account acc2 = accountService.createAccount(BigDecimal.valueOf(200.00));

        accountService.transfer(acc1.getId(), acc2.getId(), BigDecimal.valueOf(150.00));

        assertEquals(Money.of(350.00), accountService.getBalance(acc1.getId()));
        assertEquals(Money.of(350.00), accountService.getBalance(acc2.getId()));
    }

    @Test
    @DisplayName("Should prevent self-transfers")
    void transfer_SameAccount_ThrowsException() {
        Account acc = accountService.createAccount(BigDecimal.valueOf(100.00));

        assertThrows(InvalidAmountException.class, () ->
                accountService.transfer(acc.getId(), acc.getId(), BigDecimal.valueOf(10.00))
        );
    }

    @Test
    @DisplayName("Should throw exception when querying non-existent account")
    void getBalance_UnknownAccount_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        assertThrows(AccountNotFoundException.class, () -> accountService.getBalance(unknownId));
    }
}
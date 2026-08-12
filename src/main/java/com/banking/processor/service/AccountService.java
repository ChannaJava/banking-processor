package com.banking.processor.service;

import com.banking.processor.domain.Account;
import com.banking.processor.domain.Money;
import com.banking.processor.domain.TransactionRecord;
import com.banking.processor.exception.AccountNotFoundException;
import com.banking.processor.exception.InsufficientFundsException;
import com.banking.processor.exception.InvalidAmountException;
import com.banking.processor.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service class handling application transaction.
 *
 */

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Constructs the AccountService with required dependencies.
     *
     */

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Creates and persists a new bank account with the specified initial balance.
     *
     * @param initialBalance the starting balance for the account
     * @return the created {@link Account} entity
     */
    @Transactional
    public Account createAccount(BigDecimal initialBalance) {
        UUID accountId = UUID.randomUUID();
        Account account = new Account(accountId, new Money(initialBalance));
        return accountRepository.save(account);
    }

    /**
     * Retrieves the current balance for the given account ID.
     *
     * @param accountId the unique identifier of the account
     * @return the current {@link Money} balance
     * @throws AccountNotFoundException if no account exists with the specified ID
     */

    @Transactional(readOnly = true)
    public Money getBalance(UUID accountId) {
        return findAccount(accountId).getBalance();
    }

    /**
     * Retrieves the complete transaction ledger history for an account.
     *
     * @param accountId the unique identifier of the account
     * @return list of {@link TransactionRecord} instances
     * @throws AccountNotFoundException if no account exists with the specified ID
     */

    @Transactional(readOnly = true)
    public List<TransactionRecord> getTransactionHistory(UUID accountId) {
        return findAccount(accountId).getLedger();
    }

    /**
     * Deposits a monetary amount into an existing account.
     *
     * @param accountId the unique identifier of the account receiving funds
     * @param amount the value to deposit
     * @throws AccountNotFoundException if the account is not found
     * @throws IllegalArgumentException if the amount is non-positive
     */

    @Transactional
    public void deposit(UUID accountId, BigDecimal amount) {
        Account account = findAccount(accountId);
        account.deposit(new Money(amount));
        accountRepository.save(account);
    }

    /**
     * Withdraws a monetary amount from an existing account.
     *
     * @param accountId the unique identifier of the account sending funds
     * @param amount the value to withdraw
     * @throws AccountNotFoundException if the account is not found
     * @throws InsufficientFundsException if the account balance is lower than the amount
     * @throws IllegalArgumentException if the amount is non-positive
     */
    @Transactional
    public void withdraw(UUID accountId, BigDecimal amount) {
        Account account = findAccount(accountId);
        account.withdraw(new Money(amount));
        accountRepository.save(account);
    }

    /**
     * Atomically transfers funds between two distinct bank accounts.
     *
     * @param sourceId the UUID of the account sending funds
     * @param destinationId the UUID of the account receiving funds
     * @param amount the monetary value to transfer
     * @throws AccountNotFoundException if either account ID does not exist
     * @throws InsufficientFundsException if the source account lacks required balance
     * @throws IllegalArgumentException if sourceAccountId and targetAccountId are identical
     */
    @Transactional
    public void transfer(UUID sourceId, UUID destinationId, BigDecimal amount) {
        if (sourceId.equals(destinationId)) {
            throw new InvalidAmountException("Cannot transfer funds to the same account");
        }

        Money transferAmount = new Money(amount);
        Account source = findAccount(sourceId);
        Account destination = findAccount(destinationId);

        source.sendTransfer(transferAmount, destinationId);
        destination.receiveTransfer(transferAmount, sourceId);

        accountRepository.save(source);
        accountRepository.save(destination);
    }

    /**
     * Helper method to locate an account or raise a domain exception.
     *
     * @param accountId the UUID to search
     * @return the found {@link Account} entity
     * @throws AccountNotFoundException if no matching entity is located
     */
    private Account findAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
    }
}
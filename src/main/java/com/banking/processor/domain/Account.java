package com.banking.processor.domain;

import com.banking.processor.exception.InsufficientFundsException;
import com.banking.processor.exception.InvalidAmountException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root representing a bank account within the domain.
 */

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Embedded
    private Money balance;

    @Version
    private Long version; // Enforces optimistic concurrency control

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private List<TransactionRecord> ledger = new ArrayList<>();

    protected Account() {} // JPA

    /**
     * Constructs a new Account initialized with a starting balance and initial ledger record.
     */

    public Account(UUID id, Money initialBalance) {
        if (id == null) throw new IllegalArgumentException("Account ID required");
        validatePositive(initialBalance);
        this.id = id;
        this.balance = initialBalance;
        if (initialBalance.isGreaterThanZero()) {
            this.ledger.add(new TransactionRecord(TransactionType.DEPOSIT, initialBalance, "Initial Account Opening Balance"));
        }
    }


    /**
     * Deposits funds into the account and logs the operation.
     *
     */

    public void deposit(Money amount) {
        validatePositive(amount);
        this.balance = this.balance.add(amount);
        this.ledger.add(new TransactionRecord(TransactionType.DEPOSIT, amount, "Deposit operation"));
    }

    /**
     * Withdraws funds from the account if sufficient balance is available.
     */

    public void withdraw(Money amount) {
        validatePositive(amount);
        validateSufficientBalance(amount);
        this.balance = this.balance.subtract(amount);
        this.ledger.add(new TransactionRecord(TransactionType.WITHDRAWAL, amount, "Withdrawal operation"));
    }

    /**
     * Deducts funds from this account to initiate an outgoing transfer to another account.
     * Validates that the amount is positive and that sufficient balance exists
     */
    public void sendTransfer(Money amount, UUID recipientId) {
        validatePositive(amount);
        validateSufficientBalance(amount);
        this.balance = this.balance.subtract(amount);
        this.ledger.add(new TransactionRecord(TransactionType.TRANSFER_OUT, amount, "Transfer sent to account: " + recipientId));
    }

    /**
     * Credits funds to this account from an incoming transfer sent by another account.
     * Validates that the amount is positive before updating the balance
     * and adding a {@link TransactionType#TRANSFER_IN} ledger entry.
     */

    public void receiveTransfer(Money amount, UUID senderId) {
        validatePositive(amount);
        this.balance = this.balance.add(amount);
        this.ledger.add(new TransactionRecord(TransactionType.TRANSFER_IN, amount, "Transfer received from account: " + senderId));
    }

    /**
     * Validates that the provided monetary amount is non-null and strictly positive.
     *
     */
    private void validatePositive(Money amount) {
        if (amount == null || !amount.isGreaterThanZero()) {
            throw new InvalidAmountException("Operation amount must be greater than zero");
        }
    }

    /**
     * Validates that the current account balance covers the requested monetary amount
     */
    private void validateSufficientBalance(Money amount) {
        if (!this.balance.isGreaterThanOrEqual(amount)) {
            throw new InsufficientFundsException("Insufficient funds. Available: " + this.balance.getAmount() + ", Required: " + amount.getAmount());
        }
    }

    public UUID getId() { return id; }
    public Money getBalance() { return balance; }
    public List<TransactionRecord> getLedger() { return Collections.unmodifiableList(ledger); }
}
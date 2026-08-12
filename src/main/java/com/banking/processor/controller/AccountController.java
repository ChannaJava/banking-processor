package com.banking.processor.controller;

import com.banking.processor.domain.Money;
import com.banking.processor.domain.TransactionRecord;
import com.banking.processor.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.banking.processor.dto.CreateAccountRequest;
import com.banking.processor.dto.AmountRequest;
import com.banking.processor.dto.TransferRequest;
import java.util.List;
import java.util.UUID;
/**
 * REST controller providing public API endpoints for managing accounts and executing financial transactions.
 * Handles request validation, HTTP status mapping, and delegates execution to {@link AccountService}.
 *
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    /**
     * Constructs the controller with the required AccountService.
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Creates a new account with an initial balance.
     *
     * @param req request body containing initial balance constraint
     * @return response entity containing created account UUID with HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<UUID> createAccount(@Valid @RequestBody CreateAccountRequest req) {
        var account = accountService.createAccount(req.initialBalance());
        return ResponseEntity.status(HttpStatus.CREATED).body(account.getId());
    }

    /**
     * Queries the balance of a specific account.
     *
     * @param id unique account UUID
     * @return response entity containing balance object with HTTP 200 OK
     */
    @GetMapping("/{id}/balance")
    public ResponseEntity<Money> getBalance(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getBalance(id));
    }

    /**
     * Queries the ledger transaction history of an account.
     *
     * @param id unique account UUID
     * @return list of transaction history records with HTTP 200 OK
     */

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TransactionRecord>> getHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getTransactionHistory(id));
    }

    /**
     * Deposits money into an account.
     *
     * @param id unique account UUID
     * @param req request body containing deposit amount
     * @return HTTP 200 OK on success
     */
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Void> deposit(@PathVariable UUID id, @Valid @RequestBody AmountRequest req) {
        accountService.deposit(id, req.amount());
        return ResponseEntity.ok().build();
    }

    /**
     * Withdraws money from an account.
     *
     * @param id unique account UUID
     * @param req request body containing withdrawal amount
     * @return HTTP 200 OK on success
     */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable UUID id, @Valid @RequestBody AmountRequest req) {
        accountService.withdraw(id, req.amount());
        return ResponseEntity.ok().build();
    }

    /**
     * Transfers money from source account to target account.
     *
     * @param id source account UUID
     * @param req request payload containing target account ID and amount
     * @return HTTP 200 OK on success
     */
    @PostMapping("/{id}/transfer")
    public ResponseEntity<Void> transfer(@PathVariable UUID id, @Valid @RequestBody TransferRequest req) {
        accountService.transfer(id, req.targetAccountId(), req.amount());
        return ResponseEntity.ok().build();
    }
}
package com.banking.processor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request payload for transferring funds between two bank accounts.
 *
 */

public record TransferRequest(
@NotNull(message = "Target account ID cannot be null")
    UUID targetAccountId,

@NotNull(message = "Amount cannot be null")
@Positive(message = "Amount must be strictly greater than zero")
    BigDecimal amount
            ) {}

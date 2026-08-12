package com.banking.processor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Generic request payload for financial operations involving a single monetary amount
 * (e.g., deposits and withdrawals).
 */

public record AmountRequest(
@NotNull(message = "Amount cannot be null")
@Positive(message = "Amount must be strictly greater than zero")
    BigDecimal amount
            ) {}
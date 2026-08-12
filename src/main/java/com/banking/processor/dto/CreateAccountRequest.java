package com.banking.processor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * Request payload for creating a new bank account.
 */
public record CreateAccountRequest(
@NotNull(message = "Initial balance cannot be null")
@PositiveOrZero(message = "Initial balance must be zero or positive")
    BigDecimal initialBalance
            ) {}
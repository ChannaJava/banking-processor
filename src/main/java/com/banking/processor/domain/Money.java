package com.banking.processor.domain;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable value object representing a monetary amount in the domain.
 */

@Embeddable
public class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private BigDecimal amount;

    /**
     * Default constructor required by JPA.
     */

    protected Money() {} // JPA

    /**
     * Constructs a new Money object rounded to two decimal places.
     */

    public Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static Money of(double val) {
        return new Money(BigDecimal.valueOf(val));
    }

    /**
     * Adds the specified Money amount to the current amount.
     */
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    /**
     * Subtracts the specified Money amount from the current amount.
     */

    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount));
    }

    /**
     * Checks if the current amount is greater than or equal to another Money amount.
     */
    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isGreaterThanZero() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
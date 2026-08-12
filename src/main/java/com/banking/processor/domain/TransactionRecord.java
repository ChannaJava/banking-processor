package com.banking.processor.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionRecord {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Embedded
    private Money amount;

    private Instant timestamp;

    private String description;

    protected TransactionRecord() {} // JPA

    public TransactionRecord(TransactionType type, Money amount, String description) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.amount = amount;
        this.timestamp = Instant.now();
        this.description = description;
    }

    public UUID getId() { return id; }
    public TransactionType getType() { return type; }
    public Money getAmount() { return amount; }
    public Instant getTimestamp() { return timestamp; }
    public String getDescription() { return description; }
}
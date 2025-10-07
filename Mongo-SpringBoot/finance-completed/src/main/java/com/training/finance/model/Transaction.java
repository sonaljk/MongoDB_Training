package com.training.finance.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "transactions")
@Schema(description = "Details about a financial transaction")
public class Transaction {
    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String _id;
    private String txnId;
    private String accountId;
    private String type;
    private double amount;
    private String currency;
    private String status;
    private LocalDateTime date;
    private String channel;
    private String remarks;
    private Address address;
}

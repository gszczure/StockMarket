package org.example.stockmarket.model;

import jakarta.persistence.*;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String walletId;
    private String stockName;
}

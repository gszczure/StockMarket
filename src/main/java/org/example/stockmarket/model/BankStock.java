package org.example.stockmarket.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class BankStock {

    @Id
    private String name;

    private int quantity;
}

package org.example.stockmarket.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Wallet {

    @Id
    private String id;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WalletStock> stocks = new HashSet<>();
}

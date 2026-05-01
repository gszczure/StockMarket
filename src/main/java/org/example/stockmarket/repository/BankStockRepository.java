package org.example.stockmarket.repository;

import org.example.stockmarket.model.BankStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankStockRepository extends JpaRepository<BankStock, String> {
}

package org.example.stockmarket.repository;

import org.example.stockmarket.model.WalletStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletStockRepository extends JpaRepository<WalletStock, Long> {

    Optional<WalletStock> findByWalletIdAndStockName(String walletId, String stockName);

}

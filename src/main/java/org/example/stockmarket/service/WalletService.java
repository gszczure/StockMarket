package org.example.stockmarket.service;

import org.example.stockmarket.model.BankStock;
import org.example.stockmarket.model.OperationType;
import org.example.stockmarket.model.Wallet;
import org.example.stockmarket.model.WalletStock;
import org.example.stockmarket.repository.WalletRepository;
import org.example.stockmarket.repository.WalletStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletStockRepository walletStockRepository;
    private final BankService bankService;

    public WalletService(WalletRepository walletRepository, WalletStockRepository walletStockRepository,
                         BankService bankService) {
        this.walletRepository = walletRepository;
        this.walletStockRepository = walletStockRepository;
        this.bankService = bankService;
    }

    @Transactional
    public void buy(String walletId, String stockName) {

        BankStock bankStock = bankService.getStock(stockName);

        bankService.decreaseStock(stockName);

        Wallet wallet = getOrCreateWallet(walletId);

        WalletStock walletStock = getOrCreateWalletStock(wallet, bankStock);

        increaseWalletStock(walletStock);

        walletStockRepository.save(walletStock);
    }

    @Transactional
    public void sell(String walletId, String stockName) {

        getWallet(walletId);

        WalletStock walletStock = getWalletStock(walletId, stockName);

        decreaseWalletStock(walletStock);

        bankService.increaseStock(stockName);

        walletStockRepository.save(walletStock);
    }

    public void operate(String walletId, String stockName, OperationType operationType) {
        switch (operationType) {
            case BUY -> buy(walletId, stockName);
            case SELL -> sell(walletId, stockName);
        }
    }

    @Transactional(readOnly = true)
    public int getStockQuantity(String walletId, String stockName) {
        return walletStockRepository
                .findByWalletIdAndStockName(walletId, stockName)
                .map(WalletStock::getQuantity)
                .orElse(0);
    }

    public Wallet getWallet(String walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));
    }

    private Wallet getOrCreateWallet(String walletId) {
        return walletRepository.findById(walletId)
                .orElseGet(() -> walletRepository.save(
                        Wallet.builder()
                                .id(walletId)
                                .build()
                ));
    }

    private WalletStock getWalletStock(String walletId, String stockName) {
        return walletStockRepository
                .findByWalletIdAndStockName(walletId, stockName)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found in wallet"));
    }

    private WalletStock getOrCreateWalletStock(Wallet wallet, BankStock bankStock) {
        return walletStockRepository
                .findByWalletIdAndStockName(wallet.getId(), bankStock.getName())
                .orElseGet(() -> WalletStock.builder()
                        .wallet(wallet)
                        .stock(bankStock)
                        .quantity(0)
                        .build());
    }

    private void increaseWalletStock(WalletStock walletStock) {
        walletStock.setQuantity(walletStock.getQuantity() + 1);
    }

    private void decreaseWalletStock(WalletStock walletStock) {
        if (walletStock.getQuantity() <= 0) {
            throw new IllegalStateException("No stock in wallet to sell");
        }
        walletStock.setQuantity(walletStock.getQuantity() - 1);
    }
}

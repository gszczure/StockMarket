package org.example.stockmarket.service;

import org.springframework.transaction.annotation.Transactional;
import org.example.stockmarket.dto.BankStocksDto;
import org.example.stockmarket.mapper.BankMapper;
import org.example.stockmarket.model.BankStock;
import org.example.stockmarket.repository.BankStockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {

    private final BankStockRepository bankStockRepository;

    public BankService(BankStockRepository bankStockRepository) {
        this.bankStockRepository = bankStockRepository;
    }

    public BankStock getStock(String stockName) {
        return bankStockRepository.findById(stockName)
                .orElseThrow(() -> new IllegalArgumentException("Stock not found: " + stockName));
    }

    public void decreaseStock(String stockName) {
        BankStock stock = getStock(stockName);

        if (stock.getQuantity() <= 0) {
            throw new IllegalStateException("No stock available in bank: " + stockName);
        }

        stock.setQuantity(stock.getQuantity() - 1);
        bankStockRepository.save(stock);
    }

    public void increaseStock(String stockName) {
        BankStock stock = getStock(stockName);

        stock.setQuantity(stock.getQuantity() + 1);
        bankStockRepository.save(stock);
    }

    public BankStocksDto getAllStocks() {
        List<BankStock> stocks = bankStockRepository.findAll();
        return BankMapper.toResponse(stocks);
    }

    @Transactional
    public void setStocks(BankStocksDto BankStockRequest) {

        List<BankStock> stocks = BankMapper.toEntities(BankStockRequest);

        bankStockRepository.deleteAll();
        bankStockRepository.saveAll(stocks);
    }
}

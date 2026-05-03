package org.example.stockmarket.controller;

import org.example.stockmarket.dto.BankStocksDto;
import org.example.stockmarket.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public ResponseEntity<BankStocksDto> getStocks() {
        return ResponseEntity
                .ok(bankService.getAllStocks());
    }

    @PostMapping
    public ResponseEntity<Void> setStocks(@RequestBody BankStocksDto BankStockRequest) {

        bankService.setStocks(BankStockRequest);

        return ResponseEntity
                .ok()
                .build();
    }
}

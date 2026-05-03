package org.example.stockmarket.controller;

import jakarta.validation.Valid;
import org.example.stockmarket.dto.request.OperationRequestDto;
import org.example.stockmarket.dto.response.WalletResponseDto;
import org.example.stockmarket.mapper.WalletMapper;
import org.example.stockmarket.model.Wallet;
import org.example.stockmarket.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{walletId}/stocks/{stockName}")
    public ResponseEntity<Void> operate(@PathVariable String walletId, @PathVariable String stockName,
                                        @RequestBody @Valid OperationRequestDto operationRequest) {

        walletService.operate(walletId, stockName, operationRequest.getType());

        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponseDto> getWallet(@PathVariable String walletId) {

        Wallet wallet = walletService.getWallet(walletId);

        return ResponseEntity
                .ok(WalletMapper.toResponse(wallet));
    }

    @GetMapping("/{walletId}/stocks/{stockName}")
    public ResponseEntity<Integer> getStockQuantity(@PathVariable String walletId, @PathVariable String stockName) {

        int quantity = walletService.getStockQuantity(walletId, stockName);

        return ResponseEntity
                .ok(quantity);
    }
}

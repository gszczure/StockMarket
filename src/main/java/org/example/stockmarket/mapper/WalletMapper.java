package org.example.stockmarket.mapper;

import org.example.stockmarket.dto.response.StockResponseDto;
import org.example.stockmarket.dto.response.WalletResponseDto;
import org.example.stockmarket.model.Wallet;
import org.example.stockmarket.model.WalletStock;

import java.util.List;

public class WalletMapper {

    public static WalletResponseDto toResponse(Wallet wallet) {

        List<StockResponseDto> stocks = wallet.getStocks()
                .stream()
                .map(WalletMapper::mapWalletStock)
                .toList();

        return WalletResponseDto.builder()
                .id(wallet.getId())
                .stocks(stocks)
                .build();
    }

    private static StockResponseDto mapWalletStock(WalletStock walletStock) {
        return StockResponseDto.builder()
                .name(walletStock.getStock().getName())
                .quantity(walletStock.getQuantity())
                .build();
    }
}

package org.example.stockmarket.mapper;

import org.example.stockmarket.dto.BankStocksDto;
import org.example.stockmarket.dto.response.StockResponseDto;
import org.example.stockmarket.model.BankStock;

import java.util.List;

public class BankMapper {

    public static BankStocksDto toResponse(List<BankStock> stocks) {

        List<StockResponseDto> stockResponses = stocks.stream()
                .map(stock -> StockResponseDto.builder()
                        .name(stock.getName())
                        .quantity(stock.getQuantity())
                        .build())
                .toList();

        return BankStocksDto.builder()
                .stocks(stockResponses)
                .build();
    }


    private static BankStock toEntity(StockResponseDto stockResponseDto) {
        return BankStock.builder()
                .name(stockResponseDto.getName())
                .quantity(stockResponseDto.getQuantity())
                .build();
    }

    public static List<BankStock> toEntities(BankStocksDto bankStocksDto) {
        return bankStocksDto.getStocks()
                .stream()
                .map(BankMapper::toEntity)
                .toList();
    }
}

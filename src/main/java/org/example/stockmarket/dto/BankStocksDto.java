package org.example.stockmarket.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.stockmarket.dto.response.StockResponseDto;

import java.util.List;

@Builder
@Getter
public class BankStocksDto {

    private List<StockResponseDto> stocks;
}

package org.example.stockmarket.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class WalletResponseDto {

    private String id;
    private List<StockResponseDto> stocks;
}

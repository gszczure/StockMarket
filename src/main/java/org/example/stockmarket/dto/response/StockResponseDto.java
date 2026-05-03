package org.example.stockmarket.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StockResponseDto {

    private String name;
    private int quantity;
}

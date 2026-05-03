package org.example.stockmarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OperationLogDto {

    private String type;

    @JsonProperty("wallet_id")
    private String walletId;

    @JsonProperty("stock_name")
    private String stockName;
}

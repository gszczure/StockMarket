package org.example.stockmarket.mapper;

import org.example.stockmarket.dto.response.OperationLogsResponseDto;
import org.example.stockmarket.dto.OperationLogDto;
import org.example.stockmarket.model.OperationLog;

import java.util.List;

public class OperationLogMapper {

    public static OperationLogsResponseDto toResponse(List<OperationLog> logs) {

        List<OperationLogDto> responses = logs.stream()
                .map(log -> OperationLogDto.builder()
                        .type(log.getType())
                        .walletId(log.getWalletId())
                        .stockName(log.getStockName())
                        .build())
                .toList();

        return OperationLogsResponseDto.builder()
                .log(responses)
                .build();
    }
}

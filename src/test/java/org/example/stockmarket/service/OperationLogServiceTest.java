package org.example.stockmarket.service;

import org.example.stockmarket.dto.response.OperationLogsResponseDto;
import org.example.stockmarket.model.OperationLog;
import org.example.stockmarket.repository.AuditLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationLogServiceTest {

    private static final String TYPE = "buy";
    private static final String WALLET_ID = "wallet-1";
    private static final String STOCK_NAME = "apple";

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private OperationLogService operationLogService;

    @Test
    @DisplayName("Should save operation log in repository when valid data is provided")
    void log_shouldSaveOperationLog_whenValidData() {

        // when
        operationLogService.log(TYPE, WALLET_ID, STOCK_NAME);

        // then
        verify(auditLogRepository).save(any(OperationLog.class));
    }

    @Test
    @DisplayName("Should persist correct type, walletId and stockName when creating operation log")
    void log_shouldSaveCorrectData_whenLogIsCreated() {

        // when
        operationLogService.log(TYPE, WALLET_ID, STOCK_NAME);

        // then
        verify(auditLogRepository).save(argThat(log ->
                log.getType().equals(TYPE) &&
                        log.getWalletId().equals(WALLET_ID) &&
                        log.getStockName().equals(STOCK_NAME)
        ));
    }

    @Test
    @DisplayName("Should return mapped response with all logs when repository contains data")
    void getAllLogs_shouldReturnMappedResponse_whenLogsExist() {

        // given
        List<OperationLog> logs = List.of(
                createOperationLog("buy", "wallet-1", "apple"),
                createOperationLog("sell", "wallet-2", "tesla")
        );

        when(auditLogRepository.findAll()).thenReturn(logs);

        // when
        OperationLogsResponseDto result = operationLogService.getAllLogs();

        // then
        assertThat(result.getLog()).hasSize(2);
        verify(auditLogRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty response when no logs exist in repository")
    void getAllLogs_shouldReturnEmptyList_whenNoLogsExist() {

        // given
        when(auditLogRepository.findAll()).thenReturn(List.of());

        // when
        OperationLogsResponseDto result = operationLogService.getAllLogs();

        // then
        assertThat(result.getLog()).isEmpty();
        verify(auditLogRepository).findAll();
    }

    private OperationLog createOperationLog(String type, String walletId, String stockName) {
        return OperationLog.builder()
                .type(type)
                .walletId(walletId)
                .stockName(stockName)
                .build();
    }
}

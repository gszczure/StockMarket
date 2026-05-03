package org.example.stockmarket.controller;

import org.example.stockmarket.dto.OperationLogDto;
import org.example.stockmarket.dto.response.OperationLogsResponseDto;
import org.example.stockmarket.service.OperationLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperationLogController.class)
class OperationLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperationLogService operationLogService;

    @Test
    @DisplayName("Should return 200 and correct log data")
    void shouldReturn200_andCorrectLogData() throws Exception {

        when(operationLogService.getAllLogs()).thenReturn(responseDto());

        mockMvc.perform(get("/log"))
                .andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.log").isArray())
                .andExpect(jsonPath("$.log[0].type").value("buy"))
                .andExpect(jsonPath("$.log[0].wallet_id").value("wallet-1"))
                .andExpect(jsonPath("$.log[0].stock_name").value("apple"))
                .andExpect(jsonPath("$.log[1].type").value("sell"))
                .andExpect(jsonPath("$.log[1].wallet_id").value("wallet-2"))
                .andExpect(jsonPath("$.log[1].stock_name").value("tesla"));
    }

    private OperationLogsResponseDto responseDto() {
        return OperationLogsResponseDto.builder()
                .log(List.of(
                        createOperationLogDto("buy", "wallet-1", "apple"),
                        createOperationLogDto("sell", "wallet-2", "tesla")
                ))
                .build();
    }

    private OperationLogDto createOperationLogDto(String type, String walletId, String stockName) {
        return OperationLogDto.builder()
                .type(type)
                .walletId(walletId)
                .stockName(stockName)
                .build();
    }
}

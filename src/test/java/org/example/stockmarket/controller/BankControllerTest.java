package org.example.stockmarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.stockmarket.dto.BankStocksDto;
import org.example.stockmarket.dto.response.StockResponseDto;
import org.example.stockmarket.service.BankService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankController.class)
class BankControllerTest {

    private static final String STOCK_NAME = "apple";
    private static final String STOCK_NAME2 = "tesla";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BankService bankService;

    @Test
    @DisplayName("Should return 200 and correct stock data when stocks exist")
    void shouldReturn200_andCorrectStockData_whenStocksExist() throws Exception {

        when(bankService.getAllStocks()).thenReturn(responseDto());

        mockMvc.perform(get("/stocks"))
                .andExpect(status()
                        .isOk())
                .andExpect(jsonPath("$.stocks").isArray())
                .andExpect(jsonPath("$.stocks[0].name").value("apple"))
                .andExpect(jsonPath("$.stocks[0].quantity").value(5))
                .andExpect(jsonPath("$.stocks[1].name").value("tesla"))
                .andExpect(jsonPath("$.stocks[1].quantity").value(1));
    }

    @Test
    @DisplayName("Should return 200 when setting stocks")
    void shouldReturn200_whenSettingStocks() throws Exception {

        mockMvc.perform(post("/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto())))
                .andExpect(status()
                        .isOk());
    }

    private BankStocksDto requestDto() {
        return BankStocksDto.builder()
                .stocks(List.of(
                        createStockResponse(STOCK_NAME, 2),
                        createStockResponse(STOCK_NAME2, 1)
                ))
                .build();
    }

    private BankStocksDto responseDto() {
        return BankStocksDto.builder()
                .stocks(List.of(
                        createStockResponse(STOCK_NAME, 5),
                        createStockResponse(STOCK_NAME2, 1)

                ))
                .build();
    }

    private StockResponseDto createStockResponse(String name, int quantity) {
        return StockResponseDto.builder()
                .name(name)
                .quantity(quantity)
                .build();
    }
}

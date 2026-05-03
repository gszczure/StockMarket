package org.example.stockmarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.stockmarket.dto.request.OperationRequestDto;
import org.example.stockmarket.exception.NotFoundException;
import org.example.stockmarket.model.OperationType;
import org.example.stockmarket.model.Wallet;
import org.example.stockmarket.service.WalletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    private static final String WALLET_ID = "wallet-1";
    private static final String STOCK_NAME = "apple";
    private static final String UNKNOWN_STOCK = "unknown";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WalletService walletService;

    @Test
    @DisplayName("Should return 200 when BUY operation is valid")
    void shouldReturn200_whenBuyIsValid() throws Exception {

        mockMvc.perform(post("/wallets/{walletId}/stocks/{stockName}", WALLET_ID, STOCK_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest(OperationType.BUY)))
                .andExpect(status()
                        .isOk());
    }

    @Test
    @DisplayName("Should return 200 when SELL operation is valid")
    void shouldReturn200_whenSellIsValid() throws Exception {

        mockMvc.perform(post("/wallets/{walletId}/stocks/{stockName}", WALLET_ID, STOCK_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest(OperationType.SELL)))
                .andExpect(status()
                        .isOk());
    }

    @Test
    @DisplayName("Should return 400 when SELL without stock")
    void shouldReturn400_whenSellWithoutStock() throws Exception {

        doThrow(new IllegalStateException("No stock in wallet to sell"))
                .when(walletService)
                .operate(WALLET_ID, STOCK_NAME, OperationType.SELL);

        mockMvc.perform(post("/wallets/{walletId}/stocks/{stockName}", WALLET_ID, STOCK_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest(OperationType.SELL)))
                .andExpect(status()
                        .isBadRequest())
                .andExpect(jsonPath("$.message").value("No stock in wallet to sell"));
    }

    @Test
    @DisplayName("Should return 404 when stock does not exist")
    void shouldReturn404_whenStockNotFound() throws Exception {

        doThrow(new NotFoundException("Stock not found"))
                .when(walletService)
                .operate(WALLET_ID, UNKNOWN_STOCK, OperationType.BUY);

        mockMvc.perform(post("/wallets/{walletId}/stocks/{stockName}", WALLET_ID, UNKNOWN_STOCK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest(OperationType.BUY)))
                .andExpect(status()
                        .isNotFound())
                .andExpect(jsonPath("$.message").value("Stock not found"));
    }

    @Test
    @DisplayName("Should return 400 when request is invalid")
    void shouldReturn400_whenInvalidRequest() throws Exception {

        mockMvc.perform(post("/wallets/{walletId}/stocks/{stockName}", WALLET_ID, STOCK_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest()))
                .andExpect(status()
                        .isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 when wallet not found")
    void shouldReturn404_whenWalletNotFound() throws Exception {

        when(walletService.getWallet(WALLET_ID)).thenThrow(new NotFoundException("Wallet not found"));

        mockMvc.perform(get("/wallets/{walletId}", WALLET_ID))
                .andExpect(status()
                        .isNotFound())
                .andExpect(jsonPath("$.message").value("Wallet not found"));
    }

    @Test
    @DisplayName("Should return 200 when wallet exists")
    void shouldReturn200_whenWalletExists() throws Exception {

        when(walletService.getWallet(WALLET_ID)).thenReturn(new Wallet());

        mockMvc.perform(get("/wallets/{walletId}", WALLET_ID))
                .andExpect(status()
                        .isOk());
    }

    @Test
    @DisplayName("Should return 200 when getting stock quantity")
    void shouldReturn200_whenGettingStockQuantity() throws Exception {

        when(walletService.getStockQuantity(WALLET_ID, STOCK_NAME)).thenReturn(5);

        mockMvc.perform(get("/wallets/{walletId}/stocks/{stockName}", WALLET_ID, STOCK_NAME))
                .andExpect(status()
                        .isOk());
    }

    private String validRequest(OperationType type) throws Exception {
        OperationRequestDto operationRequestDto = new OperationRequestDto();
        operationRequestDto.setType(type);
        return objectMapper.writeValueAsString(operationRequestDto);
    }

    private String invalidRequest() throws Exception {
        OperationRequestDto operationRequestDto = new OperationRequestDto();
        operationRequestDto.setType(null);
        return objectMapper.writeValueAsString(operationRequestDto);
    }
}

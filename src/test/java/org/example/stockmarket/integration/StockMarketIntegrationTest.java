package org.example.stockmarket.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import org.example.stockmarket.dto.BankStocksDto;
import org.example.stockmarket.dto.request.OperationRequestDto;
import org.example.stockmarket.dto.response.StockResponseDto;
import org.example.stockmarket.model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StockMarketIntegrationTest extends IntegrationTests {

    @LocalServerPort
    int port;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String WALLET_ID = "wallet-1";
    private static final String STOCK_NAME = "apple";

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }


    @Test
    @DisplayName("Should add stock to wallet when BUY operation is executed and stock exists in bank")
    void buy_shouldAddStockToWallet_whenStockAvailable() throws Exception {

        initializeBank(5);

        given()
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(operationRequest(OperationType.BUY)))
                .when()
                .post(walletStockUrl())
                .then()
                .statusCode(200);

        given()
                .when()
                .get(walletStockUrl())
                .then()
                .statusCode(200)
                .body(equalTo("1"));
    }

    @Test
    @DisplayName("Should decrease stock in wallet when SELL operation is executed")
    void sell_shouldDecreaseStockInWallet_whenStockExists() throws Exception {

        initializeBank(5);
        buyOnce();

        given()
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(operationRequest(OperationType.SELL)))
                .when()
                .post(walletStockUrl())
                .then()
                .statusCode(200);

        given()
                .when()
                .get(walletStockUrl())
                .then()
                .statusCode(200)
                .body(equalTo("0"));
    }

    @Test
    @DisplayName("Should create logs when BUY and SELL operations are executed")
    void logs_shouldBeCreated_whenOperationsExecuted() throws Exception {

        initializeBank(5);
        buyOnce();
        sellOnce();

        given()
                .when()
                .get("/log")
                .then()
                .statusCode(200)
                .body("log", not(empty()))
                .body("log[0].type", notNullValue());
    }

    @Test
    @DisplayName("Should return 400 when BUY is executed but bank has zero stock")
    void buy_shouldReturn400_whenStockNotAvailable() throws Exception {

        initializeBank(0);

        given()
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(operationRequest(OperationType.BUY)))
                .when()
                .post(walletStockUrl())
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Should return stocks with correct quantities after initialization")
    void getStocks_shouldReturnCorrectState_afterSetStocks() throws Exception {

        initializeBank(5);

        given()
                .when()
                .get("/stocks")
                .then()
                .statusCode(200)
                .body("stocks[0].name", equalTo("apple"))
                .body("stocks[0].quantity", equalTo(5));
    }

    private void initializeBank(int quantity) throws Exception {
        given()
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(createBankStocks(quantity)))
                .when()
                .post("/stocks");
    }

    private void buyOnce() throws Exception {
        given()
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(operationRequest(OperationType.BUY)))
                .when()
                .post(walletStockUrl());
    }

    private void sellOnce() throws Exception {
        given()
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(operationRequest(OperationType.SELL)))
                .when()
                .post(walletStockUrl());
    }

    private String walletStockUrl() {
        return "/wallets/%s/stocks/%s".formatted(WALLET_ID, STOCK_NAME);
    }

    private OperationRequestDto operationRequest(OperationType type) {
        OperationRequestDto dto = new OperationRequestDto();
        dto.setType(type);
        return dto;
    }

    private BankStocksDto createBankStocks(int quantity) {
        return BankStocksDto.builder()
                .stocks(List.of(stockResponse(STOCK_NAME, quantity)))
                .build();
    }

    private StockResponseDto stockResponse(String name, int quantity) {
        return StockResponseDto.builder()
                .name(name)
                .quantity(quantity)
                .build();
    }
}

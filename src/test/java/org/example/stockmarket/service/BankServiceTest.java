package org.example.stockmarket.service;

import org.example.stockmarket.dto.BankStocksDto;
import org.example.stockmarket.dto.response.StockResponseDto;
import org.example.stockmarket.exception.NotFoundException;
import org.example.stockmarket.model.BankStock;
import org.example.stockmarket.repository.BankStockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    private static final String STOCK_NAME = "apple";
    private static final String UNKNOWN_STOCK = "unknown";

    @Mock
    private BankStockRepository bankStockRepository;

    @InjectMocks
    private BankService bankService;

    @Test
    @DisplayName("Should return stock when stock exists in repository")
    void getStock_shouldReturnStock_whenStockExists() {

        // given
        BankStock stock = createBankStock(5);
        when(bankStockRepository.findById(STOCK_NAME)).thenReturn(Optional.of(stock));

        // when
        BankStock result = bankService.getStock(STOCK_NAME);

        // then
        assertThat(result).isEqualTo(stock);
        verify(bankStockRepository).findById(STOCK_NAME);
    }

    @Test
    @DisplayName("Should throw NotFoundException when stock does not exist in repository")
    void getStock_shouldThrowNotFoundException_whenStockDoesNotExist() {

        // given
        when(bankStockRepository.findById(UNKNOWN_STOCK)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bankService.getStock(UNKNOWN_STOCK))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Stock not found");

        verify(bankStockRepository).findById(UNKNOWN_STOCK);
    }

    @Test
    @DisplayName("Should decrease stock quantity by one when stock is available")
    void decreaseStock_shouldDecreaseQuantity_whenStockAvailable() {

        // given
        BankStock stock = createBankStock(5);
        when(bankStockRepository.findById(STOCK_NAME)).thenReturn(Optional.of(stock));

        // when
        bankService.decreaseStock(STOCK_NAME);

        // then
        assertThat(stock.getQuantity()).isEqualTo(4);
        verify(bankStockRepository).save(stock);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to decrease stock with zero quantity")
    void decreaseStock_shouldThrowIllegalStateException_whenStockIsZero() {

        // given
        BankStock stock = createBankStock(0);
        when(bankStockRepository.findById(STOCK_NAME)).thenReturn(Optional.of(stock));

        // when & then
        assertThatThrownBy(() -> bankService.decreaseStock(STOCK_NAME))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No stock available in bank");
    }

    @Test
    @DisplayName("Should increase stock quantity by one when stock exists")
    void increaseStock_shouldIncreaseQuantity_whenStockExists() {

        // given
        BankStock stock = createBankStock(3);
        when(bankStockRepository.findById(STOCK_NAME)).thenReturn(Optional.of(stock));

        // when
        bankService.increaseStock(STOCK_NAME);

        // then
        assertThat(stock.getQuantity()).isEqualTo(4);
        verify(bankStockRepository).save(stock);
    }

    @Test
    @DisplayName("Should return all stocks")
    void getAllStocks_shouldReturnMappedDto_whenStocksExist() {

        // given
        List<BankStock> stocks = List.of(
                createBankStock(5),
                createBankStock(2)
        );
        when(bankStockRepository.findAll()).thenReturn(stocks);

        // when
        BankStocksDto result = bankService.getAllStocks();

        // then
        assertThat(result.getStocks()).hasSize(2);
        verify(bankStockRepository).findAll();
    }

    @Test
    @DisplayName("Should update quantity for existing stock and insert new stock")
    void setStocks_shouldUpdateAndInsert_whenRequestIsValid() {

        // given
        BankStocksDto request = createRequestDto();

        BankStock existing = createBankStock(11);

        when(bankStockRepository.findById("apple")).thenReturn(Optional.of(existing));
        when(bankStockRepository.findById("tesla")).thenReturn(Optional.empty());

        // when
        bankService.setStocks(request);

        // then
        assertThat(existing.getQuantity()).isEqualTo(5);

        verify(bankStockRepository).save(argThat(stock ->
                stock.getName().equals("tesla") && stock.getQuantity() == 2));
        verify(bankStockRepository, never()).save(existing);
    }

    private BankStock createBankStock(int quantity) {
        return BankStock.builder()
                .name(STOCK_NAME)
                .quantity(quantity)
                .build();
    }

    private BankStocksDto createRequestDto() {
        return BankStocksDto.builder()
                .stocks(List.of(
                        stockResponse("apple", 5),
                        stockResponse("tesla", 2)
                ))
                .build();
    }

    private StockResponseDto stockResponse(String name, int quantity) {
        return StockResponseDto.builder()
                .name(name)
                .quantity(quantity)
                .build();
    }
}

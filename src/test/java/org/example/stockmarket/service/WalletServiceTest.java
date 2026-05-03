package org.example.stockmarket.service;

import org.example.stockmarket.exception.NotFoundException;
import org.example.stockmarket.model.*;
import org.example.stockmarket.repository.WalletRepository;
import org.example.stockmarket.repository.WalletStockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    private static final String WALLET_ID = "wallet-1";
    private static final String STOCK_NAME = "apple";
    private static final String UNKNOWN_STOCK = "unknown";

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletStockRepository walletStockRepository;

    @Mock
    private BankService bankService;

    @Mock
    private OperationLogService logService;

    @InjectMocks
    private WalletService walletService;

    @Test
    @DisplayName("Should buy stock and increase quantity when everything is valid")
    void buy_shouldIncreaseQuantity_whenEverythingIsValid() {

        // given
        BankStock bankStock = createBankStock(1);
        Wallet wallet = createWallet();
        WalletStock walletStock = createWalletStock(wallet, bankStock, 0);

        when(bankService.getStock(STOCK_NAME)).thenReturn(bankStock);
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(walletStockRepository.findByWalletIdAndStockName(WALLET_ID, STOCK_NAME))
                .thenReturn(Optional.of(walletStock));

        // when
        walletService.buy(WALLET_ID, STOCK_NAME);

        // then
        assertThat(walletStock.getQuantity()).isEqualTo(1);

        verify(bankService).getStock(STOCK_NAME);
        verify(bankService).decreaseStock(STOCK_NAME);
        verify(walletRepository).findById(WALLET_ID);
        verify(walletStockRepository).save(walletStock);
        verify(logService).log("buy", WALLET_ID, STOCK_NAME);
    }

    @Test
    @DisplayName("Should create wallet when it does not exist during buy")
    void buy_shouldCreateWallet_whenWalletDoesNotExist() {

        // given
        BankStock bankStock = createBankStock(1);

        when(bankService.getStock(STOCK_NAME)).thenReturn(bankStock);
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.empty());
        when(walletRepository.save(any())).thenReturn(createWallet());
        when(walletStockRepository.findByWalletIdAndStockName(any(), any())).thenReturn(Optional.empty());

        // when
        walletService.buy(WALLET_ID, STOCK_NAME);

        // then
        verify(walletRepository).save(any());
        verify(bankService).decreaseStock(STOCK_NAME);
        verify(logService).log("buy", WALLET_ID, STOCK_NAME);
    }

    @Test
    @DisplayName("Should throw NotFoundException when stock does not exist in bank")
    void buy_shouldThrowNotFoundException_whenStockDoesNotExistInBank() {

        // given
        when(bankService.getStock(UNKNOWN_STOCK)).thenThrow(new NotFoundException("Stock not found"));

        // when & then
        assertThatThrownBy(() -> walletService.buy(WALLET_ID, UNKNOWN_STOCK))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Stock not found");
    }

    @Test
    @DisplayName("Should sell stock and decrease quantity")
    void sell_shouldDecreaseQuantity_whenStockExistsInWallet() {

        // given
        Wallet wallet = createWallet();
        WalletStock walletStock = createWalletStock(wallet, createBankStock(1), 1);

        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(walletStockRepository.findByWalletIdAndStockName(WALLET_ID, STOCK_NAME))
                .thenReturn(Optional.of(walletStock));

        // when
        walletService.sell(WALLET_ID, STOCK_NAME);

        // then
        assertThat(walletStock.getQuantity()).isEqualTo(0);

        verify(walletRepository).findById(WALLET_ID);
        verify(walletStockRepository).findByWalletIdAndStockName(WALLET_ID, STOCK_NAME);
        verify(bankService).increaseStock(STOCK_NAME);
        verify(walletStockRepository).save(walletStock);
        verify(logService).log("sell", WALLET_ID, STOCK_NAME);
    }

    @Test
    @DisplayName("Should throw NotFoundException when wallet does not exist during sell")
    void sell_shouldThrowNotFoundException_whenWalletDoesNotExist() {

        // given
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walletService.sell(WALLET_ID, STOCK_NAME))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Wallet not found");

        verify(walletRepository).findById(WALLET_ID);
    }

    @Test
    @DisplayName("Should throw NotFoundException when stock not in wallet")
    void sell_shouldThrowNotFoundException_whenStockNotInWallet() {

        // given
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(createWallet()));
        when(walletStockRepository.findByWalletIdAndStockName(WALLET_ID, STOCK_NAME)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walletService.sell(WALLET_ID, STOCK_NAME))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Stock not found in wallet");

        verify(walletRepository).findById(WALLET_ID);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when selling stock with zero quantity")
    void sell_shouldThrowIllegalStateException_whenQuantityIsZero() {

        // given
        Wallet wallet = createWallet();
        WalletStock walletStock = createWalletStock(wallet, createBankStock(1), 0);

        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(wallet));
        when(walletStockRepository.findByWalletIdAndStockName(WALLET_ID, STOCK_NAME))
                .thenReturn(Optional.of(walletStock));

        // when & then
        assertThatThrownBy(() -> walletService.sell(WALLET_ID, STOCK_NAME))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No stock in wallet to sell");

        verify(walletRepository).findById(WALLET_ID);
    }

    @Test
    @DisplayName("Should delegate BUY operation to buy method")
    void operate_shouldCallBuy_whenOperationTypeIsBuy() {

        // given
        WalletService spy = spy(walletService);
        doNothing().when(spy).buy(WALLET_ID, STOCK_NAME);

        // when
        spy.operate(WALLET_ID, STOCK_NAME, OperationType.BUY);

        // then
        verify(spy).buy(WALLET_ID, STOCK_NAME);
    }

    @Test
    @DisplayName("Should delegate SELL operation to sell method")
    void operate_shouldCallSell_whenOperationTypeIsSell() {

        // given
        WalletService spy = spy(walletService);
        doNothing().when(spy).sell(WALLET_ID, STOCK_NAME);

        // when
        spy.operate(WALLET_ID, STOCK_NAME, OperationType.SELL);

        // then
        verify(spy).sell(WALLET_ID, STOCK_NAME);
    }

    @Test
    @DisplayName("Should return stock quantity when exists")
    void getStockQuantity_shouldReturnQuantity_whenStockExists() {

        // given
        when(walletStockRepository.findByWalletIdAndStockName(WALLET_ID, STOCK_NAME))
                .thenReturn(Optional.of(createWalletStock(createWallet(),
                        createBankStock(1), 3)));

        // when
        int result = walletService.getStockQuantity(WALLET_ID, STOCK_NAME);

        // then
        assertThat(result).isEqualTo(3);
        verify(walletStockRepository).findByWalletIdAndStockName(WALLET_ID, STOCK_NAME);
    }

    @Test
    @DisplayName("Should return 0 when stock not found")
    void getStockQuantity_shouldReturnZero_whenStockDoesNotExist() {

        // given
        when(walletStockRepository.findByWalletIdAndStockName(WALLET_ID, STOCK_NAME))
                .thenReturn(Optional.empty());

        // when
        int result = walletService.getStockQuantity(WALLET_ID, STOCK_NAME);

        // then
        assertThat(result).isEqualTo(0);
        verify(walletStockRepository).findByWalletIdAndStockName(WALLET_ID, STOCK_NAME);
    }

    @Test
    @DisplayName("Should return wallet when exists")
    void getWallet_shouldReturnWallet_whenWalletExists() {

        // given
        Wallet wallet = createWallet();
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(wallet));

        // when
        Wallet result = walletService.getWallet(WALLET_ID);

        // then
        assertThat(result).isEqualTo(wallet);
        verify(walletRepository).findById(WALLET_ID);
    }

    @Test
    @DisplayName("Should throw NotFoundException when wallet does not exist")
    void getWallet_shouldThrowNotFoundException_whenWalletDoesNotExist() {

        // given
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> walletService.getWallet(WALLET_ID))
                .isInstanceOf(NotFoundException.class);

        verify(walletRepository).findById(WALLET_ID);
    }

    private Wallet createWallet() {
        return Wallet.builder()
                .id(WALLET_ID)
                .build();
    }

    private BankStock createBankStock(int bankQuantity) {
        return BankStock.builder()
                .name(STOCK_NAME)
                .quantity(bankQuantity)
                .build();
    }

    private WalletStock createWalletStock(Wallet wallet, BankStock stock, int walletQuantity) {
        return WalletStock.builder()
                .wallet(wallet)
                .stock(stock)
                .quantity(walletQuantity)
                .build();
    }
}

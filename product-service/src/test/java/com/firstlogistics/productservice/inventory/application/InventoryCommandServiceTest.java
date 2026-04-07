package com.firstlogistics.productservice.inventory.application;

import com.firstlogistics.productservice.inventory.application.InventoryCommandService.InventoryItem;
import com.firstlogistics.productservice.inventory.domain.entity.Inventory;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryErrorCode;
import com.firstlogistics.productservice.inventory.domain.exception.InventoryException;
import com.firstlogistics.productservice.inventory.domain.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InventoryCommandServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @InjectMocks
    private InventoryCommandService inventoryCommandService;

    private static final UUID PRODUCT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() throws InterruptedException {
        lenient().when(redissonClient.getLock(anyString())).thenReturn(rLock);
        lenient().when(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).thenReturn(true);
        lenient().when(rLock.isHeldByCurrentThread()).thenReturn(true);
    }

    @Nested
    @DisplayName("재고 예약 (reserve)")
    class Reserve {

        @Test
        @DisplayName("사용 가능한 재고가 충분하면 예약에 성공한다")
        void reserve_success() {
            // given
            Inventory inventory = Inventory.reconstitute(PRODUCT_ID, 100, 0);
            given(inventoryRepository.findByProductId(PRODUCT_ID)).willReturn(Optional.of(inventory));
            given(inventoryRepository.update(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            inventoryCommandService.reserve(List.of(new InventoryItem(PRODUCT_ID, 30)));

            // then
            assertThat(inventory.getAvailable()).isEqualTo(70);
            assertThat(inventory.getReserved()).isEqualTo(30);
            verify(inventoryRepository).update(inventory);
        }

        @Test
        @DisplayName("재고가 부족하면 INSUFFICIENT_AVAILABLE_QUANTITY 예외가 발생한다")
        void reserve_insufficientStock_throwsException() {
            // given
            Inventory inventory = Inventory.reconstitute(PRODUCT_ID, 10, 0);
            given(inventoryRepository.findByProductId(PRODUCT_ID)).willReturn(Optional.of(inventory));

            // when & then
            assertThatThrownBy(() ->
                    inventoryCommandService.reserve(List.of(new InventoryItem(PRODUCT_ID, 50))))
                    .isInstanceOf(InventoryException.class)
                    .hasMessageContaining(InventoryErrorCode.INSUFFICIENT_AVAILABLE_QUANTITY.getMessage());
        }

        @Test
        @DisplayName("재고 정보가 없으면 INVENTORY_NOT_FOUND 예외가 발생한다")
        void reserve_inventoryNotFound_throwsException() {
            // given
            given(inventoryRepository.findByProductId(PRODUCT_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    inventoryCommandService.reserve(List.of(new InventoryItem(PRODUCT_ID, 10))))
                    .isInstanceOf(InventoryException.class)
                    .hasMessageContaining(InventoryErrorCode.INVENTORY_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("락 획득에 실패하면 INVENTORY_LOCK_FAILED 예외가 발생한다")
        void reserve_lockFailed_throwsException() throws InterruptedException {
            // given
            given(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).willReturn(false);

            // when & then
            assertThatThrownBy(() ->
                    inventoryCommandService.reserve(List.of(new InventoryItem(PRODUCT_ID, 10))))
                    .isInstanceOf(InventoryException.class)
                    .hasMessageContaining(InventoryErrorCode.INVENTORY_LOCK_FAILED.getMessage());
        }
    }

    @Nested
    @DisplayName("재고 확정 (confirm)")
    class Confirm {

        @Test
        @DisplayName("예약된 재고를 확정하면 reserved가 감소한다")
        void confirm_success() {
            // given
            Inventory inventory = Inventory.reconstitute(PRODUCT_ID, 70, 30);
            given(inventoryRepository.findByProductId(PRODUCT_ID)).willReturn(Optional.of(inventory));
            given(inventoryRepository.update(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            inventoryCommandService.confirm(List.of(new InventoryItem(PRODUCT_ID, 30)));

            // then
            assertThat(inventory.getAvailable()).isEqualTo(70);
            assertThat(inventory.getReserved()).isEqualTo(0);
            verify(inventoryRepository).update(inventory);
        }

        @Test
        @DisplayName("예약 수량보다 많이 확정하면 예외가 발생한다")
        void confirm_insufficientReserved_throwsException() {
            // given
            Inventory inventory = Inventory.reconstitute(PRODUCT_ID, 70, 10);
            given(inventoryRepository.findByProductId(PRODUCT_ID)).willReturn(Optional.of(inventory));

            // when & then
            assertThatThrownBy(() ->
                    inventoryCommandService.confirm(List.of(new InventoryItem(PRODUCT_ID, 30))))
                    .isInstanceOf(InventoryException.class)
                    .hasMessageContaining(InventoryErrorCode.INSUFFICIENT_AVAILABLE_QUANTITY.getMessage());
        }

        @Test
        @DisplayName("락 획득에 실패하면 INVENTORY_LOCK_FAILED 예외가 발생한다")
        void confirm_lockFailed_throwsException() throws InterruptedException {
            // given
            given(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).willReturn(false);

            // when & then
            assertThatThrownBy(() ->
                    inventoryCommandService.confirm(List.of(new InventoryItem(PRODUCT_ID, 10))))
                    .isInstanceOf(InventoryException.class)
                    .hasMessageContaining(InventoryErrorCode.INVENTORY_LOCK_FAILED.getMessage());
        }
    }

    @Nested
    @DisplayName("재고 예약 취소 (cancel)")
    class Cancel {

        @Test
        @DisplayName("예약된 재고를 취소하면 available이 복원된다")
        void cancel_success() {
            // given
            Inventory inventory = Inventory.reconstitute(PRODUCT_ID, 70, 30);
            given(inventoryRepository.findByProductId(PRODUCT_ID)).willReturn(Optional.of(inventory));
            given(inventoryRepository.update(any())).willAnswer(invocation -> invocation.getArgument(0));

            // when
            inventoryCommandService.cancel(List.of(new InventoryItem(PRODUCT_ID, 30)));

            // then
            assertThat(inventory.getAvailable()).isEqualTo(100);
            assertThat(inventory.getReserved()).isEqualTo(0);
            verify(inventoryRepository).update(inventory);
        }

        @Test
        @DisplayName("예약 수량보다 많이 취소하면 예외가 발생한다")
        void cancel_insufficientReserved_throwsException() {
            // given
            Inventory inventory = Inventory.reconstitute(PRODUCT_ID, 70, 10);
            given(inventoryRepository.findByProductId(PRODUCT_ID)).willReturn(Optional.of(inventory));

            // when & then
            assertThatThrownBy(() ->
                    inventoryCommandService.cancel(List.of(new InventoryItem(PRODUCT_ID, 30))))
                    .isInstanceOf(InventoryException.class)
                    .hasMessageContaining(InventoryErrorCode.INSUFFICIENT_RESERVED_QUANTITY.getMessage());
        }

        @Test
        @DisplayName("락 획득에 실패하면 INVENTORY_LOCK_FAILED 예외가 발생한다")
        void cancel_lockFailed_throwsException() throws InterruptedException {
            // given
            given(rLock.tryLock(anyLong(), anyLong(), eq(TimeUnit.SECONDS))).willReturn(false);

            // when & then
            assertThatThrownBy(() ->
                    inventoryCommandService.cancel(List.of(new InventoryItem(PRODUCT_ID, 10))))
                    .isInstanceOf(InventoryException.class)
                    .hasMessageContaining(InventoryErrorCode.INVENTORY_LOCK_FAILED.getMessage());
        }
    }
}

package com.firstlogistics.deliverservice.infrastructure.redis.lock;

import com.firstlogistics.deliverservice.domain.exception.DistributedLockException;
import lombok.extern.slf4j.Slf4j;
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
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DeliveryDistributedLockServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @InjectMocks
    private DeliveryDistributedLockService lockService;

    @Nested
    @DisplayName("분산락 실패")
    class ExecuteWithMultiLockFail {

        @Test
        @DisplayName("락 획득 실패 시 DistributedLockException 발생")
        void executeWithMultiLock_fail_lockNotAcquired() throws InterruptedException {
            // given
            String lockKey = DeliveryLockKeyGenerator.hubStaffAssignKey(UUID.randomUUID());
            RLock mockLock = mock(RLock.class);

            given(redissonClient.getLock(lockKey)).willReturn(mockLock);
            given(mockLock.tryLock(anyLong(), anyLong(), any())).willReturn(false);

            // when
            Throwable throwable = catchThrowable(() ->
                lockService.executeWithMultiLock(List.of(lockKey), () -> "action")
            );
            log.info("[락 획득 실패] 발생 예외: {}", throwable.getClass().getSimpleName());

            // then
            assertThat(throwable).isInstanceOf(DistributedLockException.class);
            then(mockLock).should(never()).unlock();
        }

        @Test
        @DisplayName("tryLock InterruptedException 발생 시 DistributedLockException + 스레드 인터럽트 상태 복원")
        void executeWithMultiLock_fail_interruptedException() throws InterruptedException {
            // given
            String lockKey = DeliveryLockKeyGenerator.hubStaffAssignKey(UUID.randomUUID());
            RLock mockLock = mock(RLock.class);

            given(redissonClient.getLock(lockKey)).willReturn(mockLock);
            given(mockLock.tryLock(anyLong(), anyLong(), any())).willThrow(new InterruptedException());

            // when
            Throwable throwable = catchThrowable(() ->
                lockService.executeWithMultiLock(List.of(lockKey), () -> "action")
            );

            log.info("[InterruptedException] 발생 예외: {}, 스레드 인터럽트 복원 여부: {}",
                throwable.getClass().getSimpleName(), Thread.currentThread().isInterrupted());

            // then
            assertThat(throwable).isInstanceOf(DistributedLockException.class);
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
            Thread.interrupted();
        }

        @Test
        @DisplayName("멀티락 - 첫 번째 락 획득 후 두 번째 락 획득 실패 시 첫 번째 락만 해제")
        void executeWithMultiLock_fail_secondLockNotAcquired_firstLockReleased() throws InterruptedException {
            // given
            String firstKey = DeliveryLockKeyGenerator.hubStaffAssignKey(UUID.randomUUID());
            String secondKey = DeliveryLockKeyGenerator.hubStaffAssignKey(UUID.randomUUID());
            RLock firstLock = mock(RLock.class);
            RLock secondLock = mock(RLock.class);

            given(redissonClient.getLock(firstKey)).willReturn(firstLock);
            given(redissonClient.getLock(secondKey)).willReturn(secondLock);
            given(firstLock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
            given(firstLock.isHeldByCurrentThread()).willReturn(true);
            given(secondLock.tryLock(anyLong(), anyLong(), any())).willReturn(false);
            given(secondLock.isHeldByCurrentThread()).willReturn(false);

            // when
            Throwable throwable = catchThrowable(() ->
                lockService.executeWithMultiLock(List.of(firstKey, secondKey), () -> "action")
            );

            log.info("[멀티락 부분 실패] 1번째 락 획득 성공 후 2번째 락 획득 실패 → 1번째 락 해제 여부 검증");

            // then
            assertThat(throwable).isInstanceOf(DistributedLockException.class);
            then(firstLock).should().unlock();
            then(secondLock).should(never()).unlock();
        }
    }

    @Nested
    @DisplayName("분산락 성공")
    class ExecuteWithMultiLockSuccess {

        @Test
        @DisplayName("락 획득 성공 시 action 실행 후 unlock 호출")
        void executeWithMultiLock_success_actionExecutedAndUnlocked() throws InterruptedException {
            // given
            String lockKey = DeliveryLockKeyGenerator.hubStaffAssignKey(UUID.randomUUID());
            RLock mockLock = mock(RLock.class);

            given(redissonClient.getLock(lockKey)).willReturn(mockLock);
            given(mockLock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
            given(mockLock.isHeldByCurrentThread()).willReturn(true);

            // when
            String result = lockService.executeWithMultiLock(List.of(lockKey), () -> "result");

            log.info("[락 획득 성공] action 반환값: '{}', unlock 호출 검증", result);

            // then
            assertThat(result).isEqualTo("result");
            then(mockLock).should().unlock();
        }

        @Test
        @DisplayName("action 예외 발생 시에도 finally에서 unlock 보장")
        void executeWithMultiLock_success_unlockCalledEvenOnActionException() throws InterruptedException {
            // given
            String lockKey = DeliveryLockKeyGenerator.hubStaffAssignKey(UUID.randomUUID());
            RLock mockLock = mock(RLock.class);

            given(redissonClient.getLock(lockKey)).willReturn(mockLock);
            given(mockLock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
            given(mockLock.isHeldByCurrentThread()).willReturn(true);

            // when
            Throwable throwable = catchThrowable(() ->
                lockService.executeWithMultiLock(List.of(lockKey), () -> {
                    throw new RuntimeException("action 예외");
                })
            );

            log.info("[action 예외 발생] 발생 예외: '{}', finally unlock 보장 검증", throwable.getMessage());

            // then
            assertThat(throwable).isInstanceOf(RuntimeException.class).hasMessage("action 예외");
            then(mockLock).should().unlock();
        }

        @Test
        @DisplayName("멀티스레드 동시 접근 시 최대 1개 스레드만 임계 구역 진입")
        void executeWithMultiLock_concurrent_maxOneConcurrent() throws InterruptedException {
            // given
            String lockKey = DeliveryLockKeyGenerator.hubStaffAssignKey(UUID.randomUUID());
            RLock mockLock = mock(RLock.class);
            given(redissonClient.getLock(lockKey)).willReturn(mockLock);

            // Semaphore(1)로 분산락의 mutex 동작을 시뮬레이션
            Semaphore semaphore = new Semaphore(1);
            ThreadLocal<Boolean> heldByThread = ThreadLocal.withInitial(() -> false);

            given(mockLock.tryLock(anyLong(), anyLong(), any())).willAnswer(inv -> {
                boolean acquired = semaphore.tryAcquire(3L, TimeUnit.SECONDS);
                heldByThread.set(acquired);
                return acquired;
            });
            given(mockLock.isHeldByCurrentThread()).willAnswer(inv -> heldByThread.get());
            doAnswer(inv -> {
                if (heldByThread.get()) {
                    semaphore.release();
                    heldByThread.set(false);
                }
                return null;
            }).when(mockLock).unlock();

            int threadCount = 5;
            AtomicInteger concurrentCount = new AtomicInteger(0);
            AtomicInteger maxConcurrent = new AtomicInteger(0);
            AtomicInteger successCount = new AtomicInteger(0);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        lockService.executeWithMultiLock(List.of(lockKey), () -> {
                            int current = concurrentCount.incrementAndGet();
                            maxConcurrent.updateAndGet(prev -> Math.max(prev, current));
                            try {
                                // 임계 구역 작업 시뮬레이션
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            concurrentCount.decrementAndGet();
                            successCount.incrementAndGet();
                            return null;
                        });
                    } catch (DistributedLockException e) {
                        log.info("락 획득 실패 스레드 (정상): {}", e.getMessage());
                    } catch (Exception e) {
                        log.error("예상치 못한 예외", e);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            // when
            startLatch.countDown();
            boolean completed = doneLatch.await(15, TimeUnit.SECONDS);

            // then
            executor.shutdown();
            assertThat(completed).isTrue();
            assertThat(maxConcurrent.get()).isEqualTo(1);
            assertThat(successCount.get()).isGreaterThanOrEqualTo(1);
            log.info("완료: 성공 {}개 / 전체 {}개 / 최대 동시 진입 {}개",
                successCount.get(), threadCount, maxConcurrent.get());
        }
    }
}

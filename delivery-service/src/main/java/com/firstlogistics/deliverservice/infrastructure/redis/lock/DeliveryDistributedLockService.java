package com.firstlogistics.deliverservice.infrastructure.redis.lock;

import com.firstlogistics.deliverservice.application.port.DistributedLockPort;
import com.firstlogistics.deliverservice.infrastructure.exception.DistributedLockException;
import com.firstlogistics.deliverservice.infrastructure.exception.InfraErrorCode;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DeliveryDistributedLockService implements DistributedLockPort {

    private static final long WAIT_TIME_SECONDS = 3L;
    private static final long LEASE_TIME_SECONDS = 10L;

    private final RedissonClient redissonClient;

    @Override
    public <T> T executeWithMultiLock(List<String> lockKeys, Supplier<T> action) {
        List<RLock> locks = lockKeys.stream()
                .map(redissonClient::getLock)
                .toList();

        try {
            for (RLock lock : locks) {
                boolean locked = lock.tryLock(WAIT_TIME_SECONDS, LEASE_TIME_SECONDS, TimeUnit.SECONDS);
                if (!locked) {
                    throw new DistributedLockException(InfraErrorCode.LOCK_ACQUISITION_FAILED);
                }
            }

            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DistributedLockException(InfraErrorCode.LOCK_ACQUISITION_FAILED);
        } finally {
            unlockAll(locks);
        }
    }

    private void unlockAll(List<RLock> locks) {
        for (int i = locks.size() - 1; i >= 0; i--) {
            RLock lock = locks.get(i);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
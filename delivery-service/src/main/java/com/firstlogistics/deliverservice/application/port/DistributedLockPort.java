package com.firstlogistics.deliverservice.application.port;

import java.util.List;
import java.util.function.Supplier;

public interface DistributedLockPort {

	<T> T executeWithMultiLock(List<String> lockKeys, Supplier<T> action);
}

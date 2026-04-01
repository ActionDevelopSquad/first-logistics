package com.firstlogistics.deliverservice.application.policy;

import java.util.List;

public class PaginationPolicy {

	private static final List<Integer> ALLOWED_SIZES = List.of(10, 30, 50);
	private static final int DEFAULT_SIZE = 10;

	public static int resolveSize(int size) {
		return ALLOWED_SIZES.contains(size) ? size : DEFAULT_SIZE;
	}
}

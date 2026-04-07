package com.firstlogistics.deliverservice.infrastructure.feign.adapter;

import com.firstlogistics.deliverservice.application.port.UserPort;
import com.firstlogistics.deliverservice.application.port.dto.UserResponse;
import com.firstlogistics.deliverservice.infrastructure.feign.UserClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFeignAdapter implements UserPort {

	private final UserClient userClient;

	@Override
	public UserResponse getUser(UUID userId) {
		log.info("[Feign] user-service 사용자 조회 - userId: {}", userId);
		return userClient.getUser(userId).data();
	}

	@Override
	public List<UserResponse> findByNameOrPhone(String name, String phone) {
		log.info("[Feign] user-service 사용자 검색 - name: {}, phone: {}", name, phone);
		return userClient.findByNameOrPhone(name, phone).data();
	}
}

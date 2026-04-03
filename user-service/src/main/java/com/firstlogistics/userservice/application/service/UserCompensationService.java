package com.firstlogistics.userservice.application.service;

import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.event.DeliveryManagerAssignFailedEvent;
import com.firstlogistics.userservice.domain.event.UserAssignFailedEvent;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCompensationService {

    private final UserRepository userRepository;

    @Transactional
    public void rollbackDeliveryManagerAssign(DeliveryManagerAssignFailedEvent event) {
        User user = userRepository.findByIdNotDeleted(event.userId());

        user.deliveryManagerOver();

        userRepository.update(user);
    }

    @Transactional
    public void rollbackUserAssign(UserAssignFailedEvent event) {
        User user = userRepository.findByIdNotDeleted(event.userId());

        user.rollbackStatus();

        userRepository.update(user);
    }
}
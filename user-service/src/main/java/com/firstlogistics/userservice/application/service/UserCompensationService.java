package com.firstlogistics.userservice.application.service;

import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.event.DeliveryStaffAssignFailedEvent;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCompensationService {

    private final UserRepository userRepository;

    @Transactional
    public void rollbackDeliveryStaffAssign(DeliveryStaffAssignFailedEvent event) {
        User user = userRepository.findByIdNotDeleted(event.userId());

        user.rollbackStatus();

        userRepository.update(user);
    }
}
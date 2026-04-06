package com.firstlogistics.notificationservice.infrastructure.persistence.jpa;

import com.firstlogistics.notificationservice.domain.entity.Notification;
import com.firstlogistics.notificationservice.domain.exception.NotificationErrorCode;
import com.firstlogistics.notificationservice.domain.exception.NotificationException;
import com.firstlogistics.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Notification save(Notification notification) {
        try {
            // 1. Domain -> Entity 변환 및 저장
            NotificationJpaEntity entity = notificationMapper.toEntity(notification);
            NotificationJpaEntity savedEntity = notificationJpaRepository.save(entity);

            log.info("알림 저장 완료: ID = {}", savedEntity.getId());

            // 2. Entity -> Domain 변환 후 반환
            return notificationMapper.toDomain(savedEntity);

        } catch (DataAccessException e) {
            // AILogRepositoryImpl와 동일하게 데이터베이스 관련 예외 처리
            log.error("알림 저장 중 데이터베이스 에러 발생: user={}, error={}",
                    notification.getReceiverId(), e.getMessage());

            throw new NotificationException(NotificationErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

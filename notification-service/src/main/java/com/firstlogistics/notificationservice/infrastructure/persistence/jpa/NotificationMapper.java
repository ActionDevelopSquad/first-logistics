package com.firstlogistics.notificationservice.infrastructure.persistence.jpa;

import com.firstlogistics.notificationservice.domain.entity.Notification;
import com.firstlogistics.notificationservice.domain.vo.NotificationId;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public static NotificationJpaEntity toEntity(Notification notification) {
        return new NotificationJpaEntity(
                notification.getId().id(),
                notification.getVersion(),
                notification.getReceiverId(),
                notification.getMessageId(),
                notification.getContent(),
                notification.getType(),
                notification.getStatus(),
                notification.getMessengerType(),
                notification.getReadAt()
        );
    }


    public static Notification toDomain(NotificationJpaEntity entity) {
        return Notification.reconstitute(
                NotificationId.of(entity.getId()),
                entity.getVersion(),
                entity.getReceiverId(),
                entity.getMessageId(),
                entity.getContent(),
                entity.getType(),
                entity.getStatus(),
                entity.getMessengerType(),
                entity.getReadAt()
        );
    }
}
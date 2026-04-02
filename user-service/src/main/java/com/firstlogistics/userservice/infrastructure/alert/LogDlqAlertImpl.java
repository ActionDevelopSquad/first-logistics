package com.firstlogistics.userservice.infrastructure.alert;

import com.firstlogistics.userservice.application.port.DlqAlert;
import com.firstlogistics.userservice.domain.event.UserStatusChangedDlqEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogDlqAlertImpl implements DlqAlert {

    @Override
    public void alertStatus(UserStatusChangedDlqEvent event) {
        log.error("""
                [DLQ 운영 알림] Kafka 발행 실패 감지 - 수동 재처리 필요
                  userId       : {}
                  username     : {}
                  originalTopic: {}
                  userRole     : {}
                  previousStatus → currentStatus: {} → {}
                  errorMessage : {}
                  failedAt     : {}
                """,
                event.userId(),
                event.username(),
                event.originalTopic(),
                event.userRole(),
                event.previousStatus(),
                event.currentStatus(),
                event.errorMessage(),
                event.failedAt()
        );
    }
}
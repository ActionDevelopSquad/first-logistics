package com.firstlogistics.hubservice.hubManager.infrastructure.messaging.producer;

import com.firstlogistics.hubservice.hubManager.domain.event.HubManagerAssignFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HubManagerAssignFailedProducer {
    private static final String TOPIC = "hub.manager.assign.failed";
    private final KafkaTemplate<String, Object> hubManagerKafkaTemplate;

    public void publish(HubManagerAssignFailedEvent event){
        hubManagerKafkaTemplate.send(TOPIC, event.userId().toString(), event)
                .whenComplete((result,ex)->{
                    if(ex!=null){
                        log.error("허브 매니저 실패 이벤트 발행 실패. userId={}, organizationId={}",
                                event.userId(), event.organizationId(), ex);
                        return;
                    }
                    log.warn("허브 매니저 실패 이벤트 발행 완료. topic={}, partition={}, offset={}, userId={}",
                            TOPIC,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset(),
                            event.userId());
                });

    }
}

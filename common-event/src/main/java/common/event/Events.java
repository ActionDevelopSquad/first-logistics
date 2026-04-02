package common.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
public class Events {

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void init(ApplicationEventPublisher eventPublisher) {
        Events.eventPublisher = eventPublisher;
    }

    public static void trigger(Object event) {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);

            log.info("[Event] Triggered: {} @{}",
                    event.getClass().getSimpleName(),
                    Integer.toHexString(System.identityHashCode(event)));
        } else {
            throw new IllegalStateException("EventPublisher가 초기화되지 않았습니다. 이벤트 발행 실패: "
                    + (event != null ? event.getClass().getSimpleName() : "null"));
        }
    }
}
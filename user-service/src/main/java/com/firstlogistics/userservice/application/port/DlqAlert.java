package com.firstlogistics.userservice.application.port;

import com.firstlogistics.userservice.domain.event.UserStatusChangedDlqEvent;

public interface DlqAlert {
    void alertStatus(UserStatusChangedDlqEvent event);
}
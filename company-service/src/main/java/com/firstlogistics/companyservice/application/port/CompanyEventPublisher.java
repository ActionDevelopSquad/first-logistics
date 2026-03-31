package com.firstlogistics.companyservice.application.port;

import com.firstlogistics.companyservice.domain.event.CompanyCreatedEvent;

public interface CompanyEventPublisher {

    void publish(CompanyCreatedEvent event);
}
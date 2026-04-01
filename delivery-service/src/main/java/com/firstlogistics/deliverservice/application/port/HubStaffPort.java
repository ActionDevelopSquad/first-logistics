package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.application.port.dto.HubStaffResponse;

import java.util.UUID;

public interface HubStaffPort {

	HubStaffResponse getHubStaff(UUID staffId);
}

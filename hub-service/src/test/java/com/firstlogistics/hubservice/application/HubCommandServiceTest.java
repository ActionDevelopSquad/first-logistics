package com.firstlogistics.hubservice.application;

import com.firstlogistics.hubservice.application.dto.command.CreateHubCommand;
import com.firstlogistics.hubservice.application.dto.result.HubResult;
import com.firstlogistics.hubservice.domain.entity.Hub;
import com.firstlogistics.hubservice.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.domain.exception.HubException;
import com.firstlogistics.hubservice.domain.repository.HubRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class HubCommandServiceTest {
    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private HubCommandService hubCommandService;

    @Test
    @DisplayName("실패: 해당 허브 이름이 이미 존재하는 허브 생성")
    void createHub_fail_DuplicateName(){
        //given
        CreateHubCommand command = new CreateHubCommand(
          "서울 허브",
          "서울특별시",
          37.5665,
          127.9780
        );
        given(hubRepository.existsByHubName(command.name())).willReturn(true);

        //when

        //then
        assertThatThrownBy(()-> hubCommandService.create(command))
                .isInstanceOf(HubException.class)
                .extracting("errorCode")
                .isEqualTo(HubErrorCode.DUPLICATE_HUB_NAME);
    }

    @Test
    @DisplayName("성공: 허브 생성")
    void createHub_success(){
        //given
        CreateHubCommand command = new CreateHubCommand(
                "서울 허브",
                "서울특별시",
                37.5665,
                127.9780
        );
        given(hubRepository.existsByHubName(command.name())).willReturn(false);
        given(hubRepository.save(any(Hub.class))).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //when
        HubResult result = hubCommandService.create(command);

        //then
        assertThat(result.name()).isEqualTo("서울 허브");
        assertThat(result.roadAddress()).isEqualTo("서울특별시");
        assertThat(result.latitude()).isEqualTo(37.5665);
        assertThat(result.longitude()).isEqualTo(127.9780);
        assertThat(result.status().name()).isEqualTo("ACTIVE");

    }


}

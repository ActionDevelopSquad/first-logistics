package com.firstlogistics.companyservice.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import com.firstlogistics.companyservice.application.dto.command.CreateCompanyCommand;
import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import com.firstlogistics.companyservice.application.port.CompanyEventPublisher;
import com.firstlogistics.companyservice.application.port.HubPort;
import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.event.CompanyCreatedEvent;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import com.firstlogistics.companyservice.domain.repository.CompanyRepository;
import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyCommandServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private HubPort hubPort;

    @Mock
    private CompanyEventPublisher eventPublisher;

    @InjectMocks
    private CompanyCommandService companyCommandService;

    private static final UUID FIXED_HUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID FIXED_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private CreateCompanyCommand validCommand;

    @BeforeEach
    void setUp() {
        validCommand = new CreateCompanyCommand(
                FIXED_USER_ID,
                "테스트업체",
                "SUPPLIER",
                "서울특별시 송파구 송파대로 55",
                "3층",
                37.514,
                127.106
        );
    }

    @Test
    @DisplayName("업체 생성 성공 - 저장된 업체 정보를 반환한다")
    void register_success() {
        // given
        given(hubPort.getHubId(any(GeoLocation.class)))
                .willReturn(FIXED_HUB_ID);
        given(companyRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        CompanyResult result = companyCommandService.register(validCommand);

        // then
        assertThat(result.hubId()).isEqualTo(FIXED_HUB_ID);
        assertThat(result.userId()).isEqualTo(FIXED_USER_ID);
        assertThat(result.name()).isEqualTo("테스트업체");
        assertThat(result.type()).isEqualTo("SUPPLIER");
        assertThat(result.status()).isEqualTo(CompanyStatus.ACTIVE.name());
    }

    @Test
    @DisplayName("업체 생성 성공 - 저장 후 이벤트가 발행된다")
    void register_publishesEvent() {
        // given
        given(hubPort.getHubId(any(GeoLocation.class)))
                .willReturn(FIXED_HUB_ID);
        given(companyRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<CompanyCreatedEvent> eventCaptor = ArgumentCaptor.forClass(CompanyCreatedEvent.class);

        // when
        CompanyResult result = companyCommandService.register(validCommand);

        // then
        verify(eventPublisher).publish(eventCaptor.capture());
        CompanyCreatedEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.companyId()).isEqualTo(result.id());
        assertThat(publishedEvent.companyName()).isEqualTo("테스트업체");
    }

    @Test
    @DisplayName("업체 생성 실패 - 허브를 찾을 수 없으면 예외가 발생한다")
    void register_invalidHubId_throwsException() {
        // given
        willThrow(new CompanyException(CompanyErrorCode.INVALID_HUB_ID))
                .given(hubPort).getHubId(any(GeoLocation.class));

        // when & then
        assertThatThrownBy(() -> companyCommandService.register(validCommand))
                .isInstanceOf(CompanyException.class)
                .hasMessageContaining(CompanyErrorCode.INVALID_HUB_ID.getMessage());
    }

    @Test
    @DisplayName("업체 생성 실패 - 알 수 없는 업체 타입이면 예외가 발생한다")
    void register_invalidCompanyType_throwsException() {
        // given
        given(hubPort.getHubId(any(GeoLocation.class)))
                .willReturn(FIXED_HUB_ID);

        CreateCompanyCommand invalidTypeCommand = new CreateCompanyCommand(
                FIXED_USER_ID, "테스트업체", "INVALID_TYPE",
                "서울특별시 송파구 송파대로 55", "3층", 37.514, 127.106
        );

        // when & then
        assertThatThrownBy(() -> companyCommandService.register(invalidTypeCommand))
                .isInstanceOf(CompanyException.class)
                .hasMessageContaining(CompanyErrorCode.INVALID_COMPANY_TYPE.getMessage());
    }
}
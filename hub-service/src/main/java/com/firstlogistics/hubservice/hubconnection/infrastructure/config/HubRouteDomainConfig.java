package com.firstlogistics.hubservice.hubconnection.infrastructure.config;

import com.firstlogistics.hubservice.hubconnection.domain.service.HubRouteDomainService;
import com.firstlogistics.hubservice.hubconnection.domain.service.HubRouteDomainServiceImpl;
import com.firstlogistics.hubservice.hubconnection.domain.strategy.HubRouteStrategySelector;
import com.firstlogistics.hubservice.hubconnection.domain.strategy.HubToHubStrategy;
import com.firstlogistics.hubservice.hubconnection.domain.strategy.P2PHubHybridStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HubRouteDomainConfig {

    @Bean
    public HubToHubStrategy hubToHubStrategy(){
        return new HubToHubStrategy();
    }

    @Bean
    public P2PHubHybridStrategy p2PHubHybridStrategy(){
        return new P2PHubHybridStrategy();
    }

    @Bean
    public HubRouteStrategySelector hubRouteStrategySelector(
            HubToHubStrategy hubToHubStrategy,
            P2PHubHybridStrategy p2PHubHybridStrategy
    ){
        return new HubRouteStrategySelector(p2PHubHybridStrategy, hubToHubStrategy);
    }

    @Bean
    public HubRouteDomainService hubRouteDomainService(
            HubRouteStrategySelector hubRouteStrategySelector
    ){
        return new HubRouteDomainServiceImpl(hubRouteStrategySelector);
    }
}

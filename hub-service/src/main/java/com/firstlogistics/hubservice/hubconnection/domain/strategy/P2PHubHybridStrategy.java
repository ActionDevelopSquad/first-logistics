package com.firstlogistics.hubservice.hubconnection.domain.strategy;

import com.firstlogistics.hubservice.hub.domain.enums.HubType;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import com.firstlogistics.hubservice.hubconnection.domain.vo.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class P2PHubHybridStrategy implements HubRouteStrategy{
    private static final int HYBRID_DISTANCE_LIMIT_METERS = 250000;
    private static final Comparator<HubRoute> HUB_ROUTE_COMPARATOR =
            Comparator.comparingInt((HubRoute route) -> route.totalTime().minutes())
                    .thenComparingInt(route -> route.totalDistance().meters());

    @Override
    public HubRoute calculate(HubId sourceHubId, HubId destinationHubId, List<HubConnection> connections, Map<HubId, RouteHub> hubMap) {
        if(sourceHubId.equals(destinationHubId))
            return HubRoute.of(List.of(),sourceHubId, destinationHubId, Time.of(1), Distance.of(0),0);

        List<HubConnection> availableConnections = connections.stream()
                .filter(HubConnection::isActive)
                .filter(connection -> connection.getDistance().meters() <= HYBRID_DISTANCE_LIMIT_METERS)
                .toList();

        HubConnection directConnection = findDirectConnection(sourceHubId, destinationHubId, availableConnections);
        if(directConnection != null && directConnection.getDistance().meters() <= HYBRID_DISTANCE_LIMIT_METERS){
            return createDirectRoute(sourceHubId, destinationHubId, directConnection);
        }

        HubRoute metropolitanRoute = findBestTransitRoute(sourceHubId, destinationHubId, availableConnections, hubMap, HubType.METROPOLITAN);
        if(metropolitanRoute != null)
            return metropolitanRoute;

        HubRoute singleTransitRoute = findBestTransitRoute(sourceHubId, destinationHubId, availableConnections, hubMap, HubType.GENERAL);
        if(singleTransitRoute!=null)
            return singleTransitRoute;

        throw new HubConnectionException(HubConnectionErrorCode.HUB_CONNECTION_NOT_FOUND);
    }

    private HubConnection findDirectConnection(HubId sourceHubId, HubId destinationHubId, List<HubConnection> connections){
        return connections.stream()
                .filter(HubConnection::isActive)
                .filter(connection -> connection.getSourceHubId().equals(sourceHubId))
                .filter(connection -> connection.getDestinationHubId().equals(destinationHubId))
                .findFirst()
                .orElse(null);
    }

    private HubRoute createDirectRoute(HubId sourceHubId, HubId destinationHubId, HubConnection connection){
        HubRouteLeg leg = HubRouteLeg.of(1, sourceHubId, destinationHubId, connection.getTime(), connection.getDistance());
        return HubRoute.of(List.of(leg),sourceHubId,destinationHubId,connection.getTime(), connection.getDistance(), 1);
    }


    private HubRoute findBestTransitRoute(
            HubId sourceHubId,
            HubId destinationHubId,
            List<HubConnection> connections,
            Map<HubId, RouteHub> hubMap,
            HubType hubType
    ) {
        return hubMap.values().stream()
                .filter(RouteHub::isActive)
                .filter(hub -> hub.type() == hubType)
                .map(RouteHub::id)
                .filter(hubId -> !hubId.equals(sourceHubId))
                .filter(hubId -> !hubId.equals(destinationHubId))
                .map(transitHubId -> createSingleTransitRoute(
                        sourceHubId,
                        transitHubId,
                        destinationHubId,
                        connections
                ))
                .filter(Objects::nonNull)
                .min(HUB_ROUTE_COMPARATOR)
                .orElse(null);
    }
    private HubRoute createSingleTransitRoute(HubId sourceHubId, HubId transitHubId, HubId destinationHubId, List<HubConnection> connections){
        HubConnection firstConnection = findDirectConnection(sourceHubId, transitHubId, connections);
        HubConnection secondConnection = findDirectConnection(transitHubId, destinationHubId, connections);

        if (firstConnection == null || secondConnection == null) {
            return null;
        }

        HubRouteLeg firstLeg = HubRouteLeg.of(
                1,
                sourceHubId,
                transitHubId,
                firstConnection.getTime(),
                firstConnection.getDistance()
        );

        HubRouteLeg secondLeg = HubRouteLeg.of(
                2,
                transitHubId,
                destinationHubId,
                secondConnection.getTime(),
                secondConnection.getDistance()
        );

        int totalMinutes = firstConnection.getTime().minutes() + secondConnection.getTime().minutes();
        int totalMeters = firstConnection.getDistance().meters() + secondConnection.getDistance().meters();

        return HubRoute.of(
                List.of(firstLeg, secondLeg),
                sourceHubId,
                destinationHubId,
                Time.of(totalMinutes),
                Distance.of(totalMeters),
                2
        );
    }
}

package com.fitnest.fitnest.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MapboxService {

    @Value("${mapbox.api.url}")
    private String mapboxApiUrl;

    @Value("${mapbox.access.token}")
    private String accessToken;

    private final RestTemplate restTemplate;

    public MapboxService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getDistanceBetweenPoints(double longitude1, double latitude1, double longitude2, double latitude2) {
        String coordinates = String.format("%f,%f;%f,%f", longitude1, latitude1, longitude2, latitude2);

        String url = UriComponentsBuilder.fromHttpUrl(mapboxApiUrl + "/" + coordinates)
                .queryParam("access_token", accessToken)
                .toUriString();

        MapboxResponse response = restTemplate.getForObject(url, MapboxResponse.class);

        if (response != null && !response.getRoutes().isEmpty()) {
            return response.getRoutes().get(0).getDistance(); // Distance en mètres
        }

        throw new RuntimeException("Distance could not be calculated.");
    }
}

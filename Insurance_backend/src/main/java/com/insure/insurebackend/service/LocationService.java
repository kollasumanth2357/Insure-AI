package com.insure.insurebackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class LocationService {

    private static final String NOMINATIM_URL =
            "https://nominatim.openstreetmap.org/search?postalcode=%s&country=India&format=json";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public LocationService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public Location getCoordinatesFromPincode(String pincode) {
        if (!StringUtils.hasText(pincode)) {
            return null;
        }

        try {
            String encodedPincode = URLEncoder.encode(pincode.trim(), StandardCharsets.UTF_8);
            URI uri = URI.create(NOMINATIM_URL.formatted(encodedPincode));

            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .header("User-Agent", "INSURANCE_APP/1.0 (contact: kollasumanth2357@gmail.com)")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return null;
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (!root.isArray() || root.isEmpty()) {
                return null;
            }

            JsonNode firstResult = root.get(0);
            JsonNode latNode = firstResult.get("lat");
            JsonNode lonNode = firstResult.get("lon");
            if (latNode == null || lonNode == null || latNode.isNull() || lonNode.isNull()) {
                return null;
            }

            return new Location(latNode.asDouble(), lonNode.asDouble());
        } catch (Exception ex) {
            return null;
        }
    }
}

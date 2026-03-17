package com.insure.insurebackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insure.insurebackend.dto.LatLngResponse;
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
public class PincodeGeocodingService {

    private static final String INDIA_COUNTRY = "India";
    private static final String PINCODE_PATTERN = "\\d{6}";
    private static final String NOMINATIM_URL =
            "https://nominatim.openstreetmap.org/search?postalcode=%s&country=%s&format=json&limit=1";
    private static final String USER_AGENT =
            "INSURANCE_APP/1.0 (contact: kollasumanth2357@gmail.com)";
    private static final String API_FAILURE_MESSAGE =
            "Unable to fetch location for the provided pincode. Please try again later.";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PincodeGeocodingService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public LatLngResponse getLatLngFromPincode(String pincode) {
        String normalizedPincode = normalizePincode(pincode);

        try {
            URI uri = URI.create(NOMINATIM_URL.formatted(
                    URLEncoder.encode(normalizedPincode, StandardCharsets.UTF_8),
                    URLEncoder.encode(INDIA_COUNTRY, StandardCharsets.UTF_8)
            ));

            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .header("User-Agent", USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(API_FAILURE_MESSAGE);
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (!root.isArray() || root.isEmpty()) {
                throw new IllegalArgumentException("Invalid pincode: no location found for " + normalizedPincode);
            }

            JsonNode firstResult = root.get(0);
            JsonNode latNode = firstResult.get("lat");
            JsonNode lonNode = firstResult.get("lon");
            if (latNode == null || lonNode == null || latNode.isNull() || lonNode.isNull()) {
                throw new RuntimeException(API_FAILURE_MESSAGE);
            }

            return new LatLngResponse(latNode.asDouble(), lonNode.asDouble());
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(API_FAILURE_MESSAGE, ex);
        }
    }

    private String normalizePincode(String pincode) {
        if (!StringUtils.hasText(pincode)) {
            throw new IllegalArgumentException("Pincode is required to determine agent location");
        }

        String normalized = pincode.trim();
        if (!normalized.matches(PINCODE_PATTERN)) {
            throw new IllegalArgumentException("Invalid pincode: please provide a valid 6-digit pincode");
        }
        return normalized;
    }
}

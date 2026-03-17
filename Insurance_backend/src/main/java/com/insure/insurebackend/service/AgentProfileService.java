package com.insure.insurebackend.service;

import com.insure.insurebackend.model.AgentProfile;
import com.insure.insurebackend.model.AvailabilityStatus;
import com.insure.insurebackend.repository.AgentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgentProfileService {

    private final AgentProfileRepository agentProfileRepository;

    public AgentProfileService(AgentProfileRepository agentProfileRepository) {
        this.agentProfileRepository = agentProfileRepository;
    }

    public AgentProfile getByUserId(Long userId) {
        AgentProfile profile = agentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Agent profile not found"));
        if (profile.getIsDeleted() != null && profile.getIsDeleted()) {
            throw new IllegalArgumentException("Agent profile not found");
        }
        return profile;
    }

    @Transactional
    public AgentProfile updateAvailability(Long userId, String status) {
        AgentProfile profile = getByUserId(userId);
        profile.setAvailabilityStatus(AvailabilityStatus.valueOf(status.toUpperCase()));
        return agentProfileRepository.save(profile);
    }

    public List<AgentProfile> findNearbyAgents(double latitude, double longitude, double radiusKm) {
        return agentProfileRepository.findAll()
                .stream()
                .filter(profile -> profile.getLatitude() != null && profile.getLongitude() != null)
                .filter(profile -> profile.getSoftDeleted() == null || !profile.getSoftDeleted())
                .filter(profile -> profile.getIsDeleted() == null || !profile.getIsDeleted())
                .filter(profile -> profile.getStatus() == null || !"INACTIVE".equalsIgnoreCase(profile.getStatus()))
                .filter(profile -> distanceKm(latitude, longitude, profile.getLatitude(), profile.getLongitude()) <= radiusKm)
                .toList();
    }

    public List<AgentProfile> findActiveAgents() {
        return agentProfileRepository.findAll()
                .stream()
                .filter(profile -> profile.getSoftDeleted() == null || !profile.getSoftDeleted())
                .filter(profile -> profile.getIsDeleted() == null || !profile.getIsDeleted())
                .filter(profile -> profile.getStatus() == null || "ACTIVE".equalsIgnoreCase(profile.getStatus()))
                .toList();
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}

package com.insure.insurebackend.service;

import com.insure.insurebackend.model.AgentProfile;
import com.insure.insurebackend.model.AgentSpecialization;
import com.insure.insurebackend.model.AvailabilityStatus;
import com.insure.insurebackend.model.Role;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.AgentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AgentProfileProvisioningService {

    private final AgentProfileRepository agentProfileRepository;

    public AgentProfileProvisioningService(AgentProfileRepository agentProfileRepository) {
        this.agentProfileRepository = agentProfileRepository;
    }

    @Transactional
    public AgentProfile ensureDefaultProfile(User user) {
        if (user == null || user.getId() == null || user.getRole() != Role.AGENT) {
            return null;
        }
        return agentProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> agentProfileRepository.save(buildDefaultProfile(user)));
    }

    @Transactional
    public void ensureDefaultProfiles(List<User> users) {
        List<User> agents = users.stream()
                .filter(user -> user.getRole() == Role.AGENT)
                .toList();
        if (agents.isEmpty()) {
            return;
        }

        List<Long> userIds = agents.stream()
                .map(User::getId)
                .toList();
        Map<Long, AgentProfile> profilesByUserId = agentProfileRepository.findByUserIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(profile -> profile.getUser().getId(), profile -> profile));
        Set<Long> existingUserIds = profilesByUserId.keySet();

        List<AgentProfile> missingProfiles = agents.stream()
                .filter(agent -> !existingUserIds.contains(agent.getId()))
                .map(this::buildDefaultProfile)
                .toList();

        if (!missingProfiles.isEmpty()) {
            agentProfileRepository.saveAll(missingProfiles);
        }
    }

    private AgentProfile buildDefaultProfile(User user) {
        AgentProfile profile = new AgentProfile();
        profile.setUser(user);
        profile.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        profile.setSpecialization(AgentSpecialization.GENERAL_INSURANCE);
        profile.setExperienceYears(0);
        profile.setStatus("ACTIVE");
        profile.setRevenueGenerated(0.0);
        profile.setTotalAppointments(0);
        profile.setSoftDeleted(false);
        profile.setIsDeleted(false);
        profile.setPincode(user.getPincode());
        profile.setLatitude(user.getLatitude());
        profile.setLongitude(user.getLongitude());
        return profile;
    }
}

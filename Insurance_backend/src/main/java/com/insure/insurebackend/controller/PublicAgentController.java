package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.NearbyAgentResponse;
import com.insure.insurebackend.model.AgentProfile;
import com.insure.insurebackend.service.AdminAgentService;
import com.insure.insurebackend.service.AgentProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agents")
@CrossOrigin(origins = "http://localhost:5173")
public class PublicAgentController {

    private final AgentProfileService agentProfileService;
    private final AdminAgentService adminAgentService;

    public PublicAgentController(AgentProfileService agentProfileService,
                                 AdminAgentService adminAgentService) {
        this.agentProfileService = agentProfileService;
        this.adminAgentService = adminAgentService;
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyAgentResponse>> nearbyAgents(@RequestParam double lat,
                                                                  @RequestParam double lng) {
        List<AgentProfile> agents = agentProfileService.findNearbyAgents(lat, lng, 10.0);
        List<NearbyAgentResponse> response = agents.stream()
                .map(this::toNearbyAgentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<List<NearbyAgentResponse>> activeAgents() {
        List<AgentProfile> agents = agentProfileService.findActiveAgents();
        List<NearbyAgentResponse> response = agents.stream()
                .map(this::toNearbyAgentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<?> deleteAgent(@PathVariable Long id) {
        adminAgentService.deleteAgent(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Agent deleted"));
    }

    @PutMapping("/{id}/soft-delete")
    public ResponseEntity<?> softDeleteAgent(@PathVariable Long id) {
        adminAgentService.softDeleteAgent(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Agent deactivated"));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateAgent(@PathVariable Long id) {
        adminAgentService.deactivateAgent(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Agent deactivated"));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateAgent(@PathVariable Long id) {
        adminAgentService.activateAgent(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Agent activated"));
    }

    private NearbyAgentResponse toNearbyAgentResponse(AgentProfile profile) {
        NearbyAgentResponse response = new NearbyAgentResponse();
        response.setId(profile.getId());
        response.setFullName(profile.getUser().getFullName());
        response.setPhone(profile.getUser().getPhone());
        response.setSpecialization(formatSpecialization(profile.getSpecialization().name()));
        response.setPincode(profile.getPincode());
        response.setLatitude(profile.getLatitude());
        response.setLongitude(profile.getLongitude());
        return response;
    }

    private String formatSpecialization(String specialization) {
        return specialization.replace("_", " ");
    }
}

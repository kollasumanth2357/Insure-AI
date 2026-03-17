package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.AgentByPincodeResponse;
import com.insure.insurebackend.service.AgentLookupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = "http://localhost:5173")
public class AgentLookupController {

    private final AgentLookupService agentLookupService;

    public AgentLookupController(AgentLookupService agentLookupService) {
        this.agentLookupService = agentLookupService;
    }

    @GetMapping("/pincode/{pincode}")
    public ResponseEntity<List<AgentByPincodeResponse>> agentsByPincode(@PathVariable String pincode) {
        return ResponseEntity.ok(agentLookupService.findAgentsByPincode(pincode));
    }
}

package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.AgentByPincodeResponse;
import com.insure.insurebackend.repository.AgentByPincodeView;
import com.insure.insurebackend.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentLookupService {

    private final AddressRepository addressRepository;

    public AgentLookupService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<AgentByPincodeResponse> findAgentsByPincode(String pincode) {
        return addressRepository.findAgentsByPincode(pincode)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AgentByPincodeResponse toResponse(AgentByPincodeView agent) {
        AgentByPincodeResponse response = new AgentByPincodeResponse();
        response.setId(agent.getId());
        response.setName(agent.getName());
        response.setPincode(agent.getPincode());
        response.setLatitude(agent.getLatitude());
        response.setLongitude(agent.getLongitude());
        return response;
    }
}

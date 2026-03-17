package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.AddressDto;
import com.insure.insurebackend.dto.AgentCreateRequest;
import com.insure.insurebackend.dto.AgentDirectoryResponse;
import com.insure.insurebackend.dto.AgentUpdateRequest;
import com.insure.insurebackend.dto.LatLngResponse;
import com.insure.insurebackend.model.*;
import com.insure.insurebackend.repository.AddressRepository;
import com.insure.insurebackend.repository.AgentProfileRepository;
import com.insure.insurebackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminAgentService {

    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final AgentProfileProvisioningService agentProfileProvisioningService;
    private final PincodeGeocodingService pincodeGeocodingService;

    public AdminAgentService(UserRepository userRepository,
                             AgentProfileRepository agentProfileRepository,
                             AddressRepository addressRepository,
                             PasswordEncoder passwordEncoder,
                             AgentProfileProvisioningService agentProfileProvisioningService,
                             PincodeGeocodingService pincodeGeocodingService) {
        this.userRepository = userRepository;
        this.agentProfileRepository = agentProfileRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        this.agentProfileProvisioningService = agentProfileProvisioningService;
        this.pincodeGeocodingService = pincodeGeocodingService;
    }

    @Transactional
    public AgentProfile createAgent(AgentCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        LatLngResponse latLng = pincodeGeocodingService.getLatLngFromPincode(request.getPincode());

        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.AGENT);
        user.setProfileImage(null);
        user.setPincode(request.getPincode());
        user.setLatitude(latLng.getLatitude());
        user.setLongitude(latLng.getLongitude());

        User savedUser = userRepository.save(user);

        AgentProfile profile = agentProfileProvisioningService.ensureDefaultProfile(savedUser);
        profile.setExperienceYears(request.getExperienceYears());
        profile.setSpecialization(resolveSpecialization(request.getSpecialization()));
        profile.setServiceAreas(request.getServiceAreas());
        profile.setAvailabilityStatus(resolveAvailability(request.getAvailabilityStatus()));
        profile.setPincode(request.getPincode());
        profile.setLatitude(latLng.getLatitude());
        profile.setLongitude(latLng.getLongitude());

        AgentProfile savedProfile = agentProfileRepository.save(profile);

        if (request.getAddress() != null) {
            Address address = mapAddress(request.getAddress(), savedUser);
            addressRepository.save(address);
        }

        return savedProfile;
    }

    @Transactional
    public AgentProfile updateAgent(Long agentId, AgentUpdateRequest request) {
        AgentProfile profile = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        User user = profile.getUser();

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getPincode() != null) {
            user.setPincode(request.getPincode());
        }
        if (request.getLatitude() != null) {
            user.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            user.setLongitude(request.getLongitude());
        }
        userRepository.save(user);

        if (request.getExperienceYears() != null) {
            profile.setExperienceYears(request.getExperienceYears());
        }
        if (request.getSpecialization() != null) {
            profile.setSpecialization(resolveSpecialization(request.getSpecialization()));
        }
        if (request.getServiceAreas() != null) {
            profile.setServiceAreas(request.getServiceAreas());
        }
        if (request.getAvailabilityStatus() != null) {
            profile.setAvailabilityStatus(resolveAvailability(request.getAvailabilityStatus()));
        }
        if (request.getPincode() != null) {
            profile.setPincode(request.getPincode());
        }
        if (request.getLatitude() != null) {
            profile.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            profile.setLongitude(request.getLongitude());
        }
        if (request.getActive() != null) {
            profile.setSoftDeleted(!request.getActive());
        }

        if (request.getAddress() != null) {
            Address address = addressRepository.findByUserId(user.getId())
                    .orElseGet(Address::new);
            mapAddressInto(address, request.getAddress(), user);
            addressRepository.save(address);
        }

        return agentProfileRepository.save(profile);
    }

    @Transactional
    public AgentProfile softDeleteAgent(Long agentId) {
        AgentProfile profile = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        profile.setSoftDeleted(true);
        profile.setIsDeleted(true);
        profile.setStatus("INACTIVE");
        return agentProfileRepository.save(profile);
    }

    @Transactional
    public AgentProfile deleteAgent(Long agentId) {
        AgentProfile profile = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        profile.setIsDeleted(true);
        profile.setSoftDeleted(true);
        profile.setStatus("INACTIVE");
        return agentProfileRepository.save(profile);
    }

    @Transactional
    public AgentProfile setAgentStatus(Long agentId, boolean active) {
        AgentProfile profile = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        profile.setSoftDeleted(!active);
        profile.setStatus(active ? "ACTIVE" : "INACTIVE");
        return agentProfileRepository.save(profile);
    }

    @Transactional
    public AgentProfile activateAgent(Long agentId) {
        AgentProfile profile = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        profile.setStatus("ACTIVE");
        return agentProfileRepository.save(profile);
    }

    @Transactional
    public AgentProfile deactivateAgent(Long agentId) {
        AgentProfile profile = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        profile.setStatus("INACTIVE");
        return agentProfileRepository.save(profile);
    }

    @Transactional
    public List<AgentDirectoryResponse> getAllAgents() {
        agentProfileProvisioningService.ensureDefaultProfiles(userRepository.findAgents());
        return userRepository.findAgentDirectory(Role.AGENT);
    }

    private AvailabilityStatus resolveAvailability(String status) {
        if (status == null) {
            return AvailabilityStatus.AVAILABLE;
        }
        return AvailabilityStatus.valueOf(status.toUpperCase());
    }

    private AgentSpecialization resolveSpecialization(String specialization) {
        if (specialization == null) {
            return AgentSpecialization.CAR;
        }
        String normalized = specialization.toUpperCase().replace(" ", "_");
        return AgentSpecialization.valueOf(normalized);
    }

    private Address mapAddress(AddressDto dto, User user) {
        Address address = new Address();
        mapAddressInto(address, dto, user);
        return address;
    }

    private void mapAddressInto(Address address, AddressDto dto, User user) {
        address.setUser(user);
        address.setDoorNo(dto.getDoorNo());
        address.setBuildingName(dto.getBuildingName());
        address.setStreet(dto.getStreet());
        address.setArea(dto.getArea());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setState(dto.getState());
        address.setPincode(dto.getPincode());
    }
}

package com.insure.insurebackend.service;

import com.insure.insurebackend.model.*;
import com.insure.insurebackend.repository.AddressRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    // ================= FIND ADDRESS =================
    public Optional<Address> findByUser(User user) {
        return addressRepository.findByUser(user);
    }

    // ================= SAVE OR UPDATE ADDRESS =================
    @Transactional
    public Address saveOrUpdateAddress(User user, ProfileRequest request) {

        Address address = addressRepository
                .findByUser(user)
                .orElseGet(() -> {
                    Address newAddress = new Address();
                    newAddress.setUser(user);
                    return newAddress;
                });

        // Update fields safely
        address.setDoorNo(request.getDoorNo());
        address.setBuildingName(request.getBuildingName());
        address.setStreet(request.getStreet());
        address.setArea(request.getArea());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());   // ✅ Added
        address.setState(request.getState());
        address.setPincode(request.getPincode());

        return addressRepository.save(address);
    }
}
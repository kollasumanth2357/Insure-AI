package com.insure.insurebackend.repository;

public interface AgentByPincodeView {
    Long getId();
    String getName();
    String getPincode();
    Double getLatitude();
    Double getLongitude();
}

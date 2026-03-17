package com.insure.insurebackend.model;

public class AgentAppointment {

    private Long id;
    private String customerName;
    private String policyCategory;
    private String policyName;
    private String dateTime;
    private String status;

    public AgentAppointment(Long id,
                            String customerName,
                            String policyCategory,
                            String policyName,
                            String dateTime,
                            String status) {
        this.id = id;
        this.customerName = customerName;
        this.policyCategory = policyCategory;
        this.policyName = policyName;
        this.dateTime = dateTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPolicyCategory() {
        return policyCategory;
    }

    public String getPolicyName() {
        return policyName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getStatus() {
        return status;
    }
}


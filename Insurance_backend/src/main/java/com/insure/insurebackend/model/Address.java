package com.insure.insurebackend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String doorNo;
    private String buildingName;
    private String street;
    private String area;
    private String city;
    private String district;
    private String state;
    private String pincode;

    // ✅ Prevent infinite recursion
    @JsonIgnore

    // ✅ Lazy loading for performance
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Address() {}

    public Address(Long id, String doorNo, String buildingName, String street,
                   String area, String city, String district,
                   String state, String pincode, User user) {
        this.id = id;
        this.doorNo = doorNo;
        this.buildingName = buildingName;
        this.street = street;
        this.area = area;
        this.city = city;
        this.district = district;
        this.state = state;
        this.pincode = pincode;
        this.user = user;
    }

    // GETTERS AND SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDoorNo() { return doorNo; }
    public void setDoorNo(String doorNo) { this.doorNo = doorNo; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
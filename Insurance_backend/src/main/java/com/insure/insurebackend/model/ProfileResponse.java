package com.insure.insurebackend.model;

public class ProfileResponse {

    // ================= USER FIELDS =================
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String profileImage;

    // ================= ADDRESS FIELDS =================
    private String doorNo;
    private String buildingName;
    private String street;
    private String area;
    private String city;
    private String district;
    private String state;
    private String pincode;

    public ProfileResponse() {}

    public ProfileResponse(Long id,
                           String fullName,
                           String username,
                           String email,
                           String phone,
                           String profileImage,
                           String doorNo,
                           String buildingName,
                           String street,
                           String area,
                           String city,
                           String district,
                           String state,
                           String pincode) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.profileImage = profileImage;
        this.doorNo = doorNo;
        this.buildingName = buildingName;
        this.street = street;
        this.area = area;
        this.city = city;
        this.district = district;
        this.state = state;
        this.pincode = pincode;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

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
}

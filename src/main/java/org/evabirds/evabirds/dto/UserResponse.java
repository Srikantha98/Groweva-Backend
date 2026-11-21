package org.evabirds.evabirds.dto;

import org.evabirds.evabirds.entity.User;

public class UserResponse {

    private Long id;
    private String email;
    private String role;
    private boolean enabled;
    private String fullName;
    private String name;
    private String phone;
    private String profileImageUrl;

    public UserResponse() {
    }

    public UserResponse(Long id, String email, String role, boolean enabled) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }

    public static UserResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled()
        );

        if (user.getProfile() != null) {
            String profileName = user.getProfile().getFullName();
            response.setFullName(profileName);
            response.setName(profileName);
            response.setPhone(user.getProfile().getPhone());
            response.setProfileImageUrl(user.getProfile().getProfileImageUrl());
        }

        return response;
    }

    public static UserResponse fromEmployee(org.evabirds.evabirds.entity.Employee emp) {
        if (emp == null) {
            return null;
        }

        UserResponse response = new UserResponse(
                emp.getId(),
                emp.getEmail(),
                emp.getRole(),
                emp.isEnabled()
        );
        response.setFullName(emp.getFullName());
        response.setName(emp.getFullName());
        response.setPhone(emp.getPhoneNumber());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name != null ? name : fullName;
    }

    public void setName(String name) {
        this.name = name;
        if (this.fullName == null) {
            this.fullName = name;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

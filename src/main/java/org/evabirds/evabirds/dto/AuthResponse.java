package org.evabirds.evabirds.dto;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private String type = "Bearer";
    private Long userId;
    private String name;
    private String fullName;
    private String email;
    private String phone;
    private String role = "ROLE_USER";
    private String profileImageUrl;
    private UserResponse user;

    public AuthResponse() {
    }

    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.tokenType = "Bearer";
        this.type = "Bearer";
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
            this.fullName = user.getFullName();
            this.name = user.getName() != null ? user.getName() : user.getFullName();
            this.email = user.getEmail();
            this.phone = user.getPhone();
            this.role = user.getRole() != null ? user.getRole() : "ROLE_USER";
            this.profileImageUrl = user.getProfileImageUrl();
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
        this.type = tokenType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.tokenType = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getFullName() {
        return fullName != null ? fullName : name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        if (this.name == null) {
            this.name = fullName;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role != null ? role : "ROLE_USER";
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
        if (user != null) {
            if (this.userId == null) this.userId = user.getId();
            if (this.fullName == null) this.fullName = user.getFullName();
            if (this.name == null) this.name = user.getName() != null ? user.getName() : user.getFullName();
            if (this.email == null) this.email = user.getEmail();
            if (this.phone == null) this.phone = user.getPhone();
            if (this.role == null) this.role = user.getRole();
            if (this.profileImageUrl == null) this.profileImageUrl = user.getProfileImageUrl();
        }
    }
}

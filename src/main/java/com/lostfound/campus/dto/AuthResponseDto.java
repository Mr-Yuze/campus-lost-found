package com.lostfound.campus.dto;

import com.lostfound.campus.entity.Role;

public class AuthResponseDto {
    private boolean success;
    private String message;
    private String token;
    private UserData user;

    public AuthResponseDto(boolean success, String message, String token, UserData user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public AuthResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UserData getUser() { return user; }
    public void setUser(UserData user) { this.user = user; }

    public static class UserData {
        private Long id;
        private String name;
        private String email;
        private Role role;

        public UserData(Long id, String name, String email, Role role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public Role getRole() { return role; }
    }
}

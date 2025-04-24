package com.unchk.unchkBackend.dto.user;

// public class LoginResponse {
//     private String token;
    
//     public LoginResponse() {}
    
//     public LoginResponse(String token) {
//         this.token = token;
//     }
    
//     public String getToken() {
//         return token;
//     }
  
// }


public class LoginResponse {
    private String token;
    private UserResponse user;

    public LoginResponse() {}

    public LoginResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
package com.unchk.unchkBackend.dto.user;

public class ResetPasswordRequest {
    private String confirmPassword;
    private String newPassword;

    public ResetPasswordRequest() {}

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public ResetPasswordRequest(String confirmPassword, String newPassword) {
        this.confirmPassword = confirmPassword;
        this.newPassword = newPassword;
    }
}


package com.unchk.unchkBackend.dto.user;

public class UpdateStudentRequest {
    private String firstName;
    private String lastName;

    public UpdateStudentRequest() {}

    public UpdateStudentRequest(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters & Setters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

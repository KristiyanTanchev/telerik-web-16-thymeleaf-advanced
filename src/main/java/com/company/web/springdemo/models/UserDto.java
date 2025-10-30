package com.company.web.springdemo.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDto {

    @Size(min = 2, max = 20, message="Username must be at least 2 and at most 20 characters.")
    private String username;


    @Size(min = 3, max = 20, message = "Password must be at least 3 and at most 20 characters.")
    private String password;

    @Size(min = 2, max = 20, message = "Password must be at least 3 and at most 20 characters.")
    private String passwordConfirm;

    @Size(min = 2, max = 20, message="First name must be at least 2 and at most 20 characters.")
    private String firstName;

    @Size(min = 2, max = 20, message="Last name must be at least 2 and at most 20 characters.")
    private String lastName;

    @NotEmpty
    @Email(message = "Please enter a valid email.")
    private String email;

    public UserDto() {
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

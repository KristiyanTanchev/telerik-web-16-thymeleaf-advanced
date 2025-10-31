package com.company.web.springdemo.exceptions;

public class UsernameDuplicateException extends EntityDuplicateException {
    public UsernameDuplicateException(String username) {
        super(String.format("Username with name %s already exists", username));
    }
}

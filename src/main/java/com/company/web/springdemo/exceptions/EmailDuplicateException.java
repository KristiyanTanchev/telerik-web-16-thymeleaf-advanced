package com.company.web.springdemo.exceptions;

public class EmailDuplicateException extends EntityDuplicateException {
    public EmailDuplicateException(String email) {
        super(String.format("User with email %s already exists.", email));
    }
}

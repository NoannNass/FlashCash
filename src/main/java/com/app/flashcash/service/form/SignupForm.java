package com.app.flashcash.service.form;

import lombok.Data;

@Data
public class SignupForm {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;

}

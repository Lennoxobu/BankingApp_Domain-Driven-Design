package com.example.BankingAppCRUD.Application.DTOs.Requests.User;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {

    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String address;
    private String password;
}

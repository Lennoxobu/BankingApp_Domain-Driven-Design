package com.example.BankingAppCRUD.Application.DTOs;

public record UserResponseWithCredentials(UserDTO userDTO, String passwordHash ) {
}

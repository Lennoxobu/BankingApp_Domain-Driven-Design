package com.example.BankingAppCRUD.Infrastructure.Config.Security.DTOs;

import com.example.BankingAppCRUD.Application.DTOs.Requests.User.UserDTO;

public record UserResponseWithCredentials(UserDTO userDTO, String passwordHash ) {
}

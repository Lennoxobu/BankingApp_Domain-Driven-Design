package com.example.BankingAppCRUD.Application.DTOs.Requests.User;

import com.example.BankingAppCRUD.Domain.ValueObject.Role;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record  UserDTO (String firstName , String lastName , String userName , String email, String address , String password , UUID id , List<Role> roles) {
}

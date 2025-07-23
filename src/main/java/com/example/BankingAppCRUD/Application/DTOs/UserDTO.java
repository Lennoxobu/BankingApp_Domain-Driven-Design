package com.example.BankingAppCRUD.Application.DTOs;

import com.example.BankingAppCRUD.Domain.ValueObject.Name;
import lombok.Builder;

@Builder
public record  UserDTO (String firstName , String lastName , String userName , String email, String address , String password ) {
}

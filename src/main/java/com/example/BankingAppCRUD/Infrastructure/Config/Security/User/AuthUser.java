package com.example.BankingAppCRUD.Infrastructure.Config.Security.User;

import com.example.BankingAppCRUD.Domain.ValueObject.Role;

import java.util.List;
import java.util.UUID;

public record AuthUser (UUID authId , List<Role> roles ) {


}

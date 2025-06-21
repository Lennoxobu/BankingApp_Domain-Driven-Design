package com.example.BankingAppCRUD.Domain.Entity.User.Model;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Domain.ValueObject.Name;
import com.example.BankingAppCRUD.Domain.ValueObject.Role;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@Table(name="_User")
@Builder
public class User {

   private UUID user_id;

   @Embedded
   private Name  user_name;
   private String user_username;
   private String user_email;
   private String user_address;
   private String password;

   private Role  user_role;
   private Timestamp createdAt;
   private Timestamp lastLoginAt;

   @OneToMany
   private UUID accountId;









}

package com.example.BankingAppCRUD.Domain.Entity.User.Model;

import com.example.BankingAppCRUD.Domain.ValueObject.AccountStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.Name;
import com.example.BankingAppCRUD.Domain.ValueObject.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@Table(name="_User")
@Where(clause= "deleted = false" )
@Builder
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   private UUID user_id;

   @Embedded
   private Name  user_name;
   private String username;
   private String user_email;
   private String user_address;
   private String hashed_password;

   private AccountStatus status;

   private List<Role>  user_roles;
   private Timestamp createdAt;
   private Timestamp lastLoginAt;


   @JoinColumns({
           @JoinColumn(name = "_CheckingAccount.id" , referencedColumnName = "_CheckingAccount.id" ),
           @JoinColumn(name = "_SavingAccount.id" , referencedColumnName = "_SavingAccount.id")
   })
   @Embedded
   private List<UUID> accountIds;

   @Column (name = "deleted" )
   @NonNull
   private boolean deleted = false;









}

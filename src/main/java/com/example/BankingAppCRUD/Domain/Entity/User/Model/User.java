package com.example.BankingAppCRUD.Domain.Entity.User.Model;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import com.example.BankingAppCRUD.Domain.ValueObject.AccountStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.Name;
import com.example.BankingAppCRUD.Domain.ValueObject.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@Table(name = "_User")
@Where(clause = "deleted = false")
@Builder
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID user_id;

    @Embedded
    @JoinColumns({
            @JoinColumn(name = "first" ,  referencedColumnName  = "first_name"),
            @JoinColumn(name = "last", referencedColumnName = "last_name"),
            @JoinColumn(name = "knownAs" , referencedColumnName = "know_as_name")
    })
    private Name user_name;
    private String username;
    private String user_email;
    private String user_address;
    private String hashed_password;

    private AccountStatus status;


    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<Role> user_roles;
    private Timestamp createdAt;
    private Timestamp lastLoginAt;



    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private List<Account> accountIds;


    @Column(name = "deleted")
    @NonNull
    private boolean deleted = false;


}

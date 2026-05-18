package com.example.BankingAppCRUD.Domain.Entity.Account.Model;


import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import com.example.BankingAppCRUD.Domain.ValueObject.AccountInfo;
import com.example.BankingAppCRUD.Domain.ValueObject.AccountStatus;
import com.example.BankingAppCRUD.Domain.ValueObject.Money;
import com.example.BankingAppCRUD.Domain.ValueObject.Rate;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public abstract class Account {

    @Enumerated(EnumType.STRING)
    protected AccountStatus account_status;
    protected Timestamp createdAt;

    @Embedded
    protected AccountInfo info;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , columnDefinition = "uuid")
    private User user;


    @Embedded
    @AttributeOverride(name = "amount" , column = @Column(name = "balance_amount"))
    @AttributeOverride(name = "currency" , column = @Column(name = "balance_currency"))
    protected Money balance;


    @OneToMany(cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    @JoinColumn(name = "transactions_id" , columnDefinition = "uuid")
    protected List<FundTransaction> account_transactions;

    @Embedded
    protected Rate rate;


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    protected UUID id;


    @Column(name = "deleted")
    protected boolean deleted =  false;



}

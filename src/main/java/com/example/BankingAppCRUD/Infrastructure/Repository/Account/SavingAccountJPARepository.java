package com.example.BankingAppCRUD.Infrastructure.Repository.Account;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Transactional
public interface SavingAccountJPARepository extends JpaRepository<SavingAccount, UUID> {




}

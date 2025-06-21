package com.example.BankingAppCRUD.Application.Controllers.AccountController;


import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Infrastructure.Service.Account.SavingAccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/SavingAccount")
public class SavingAccountController {

    @Autowired
    SavingAccountServiceImpl savingAccountServiceImpl;

    @Autowired
    SavingAccountController (SavingAccountServiceImpl savingAccountServiceImpl) {
        this.savingAccountServiceImpl = savingAccountServiceImpl;

    }



    @GetMapping
    public Page<SavingAccount> getAllAccounts (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return this.savingAccountServiceImpl.getAllAccounts(pageable);
    }



}

package com.example.BankingAppCRUD.Account.Controller;


import com.example.BankingAppCRUD.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Account.Service.SavingAccountService;
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
    SavingAccountService savingAccountService;

    @Autowired
    SavingAccountController (SavingAccountService savingAccountService) {
        this.savingAccountService = savingAccountService;

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
        return this.savingAccountService.getAllAccounts(pageable);
    }


}

package com.example.BankingAppCRUD.Account.Controller;

import com.example.BankingAppCRUD.Account.Model.AccountRequest;
import com.example.BankingAppCRUD.Account.Model.AccountResponse;
import com.example.BankingAppCRUD.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Account.Service.CheckingAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/CheckingAccount")
public class CheckingAccountController {

    private CheckingAccountService checkingAccountService;


    @Autowired
    CheckingAccountController (CheckingAccountService checkingAccountService) {
        this.checkingAccountService = checkingAccountService;
    }




    @PostMapping
    public ResponseEntity<CheckingAccount> createAccount (AccountRequest checkingAccountRequest) {
        return new ResponseEntity<>(checkingAccountService.createAccount(checkingAccountRequest) , HttpStatus.CREATED);
    }





    @GetMapping
    public Page<CheckingAccount> getAllAccounts (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return this.checkingAccountService.getAllAccounts(pageable);
    }


    @GetMapping
    public ResponseEntity<Optional<AccountRequest>> getAccount (long Id) {

        return ResponseEntity.ok(checkingAccountService.getAccountById(Id));
    }



    @PostMapping
    public ResponseEntity<AccountResponse> submitAccount (AccountRequest accDTO ) {


        checkingAccountService.createAccount(AccountRequest);
    }


}

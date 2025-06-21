package com.example.BankingAppCRUD.Application.Controllers.AccountController;

import com.example.BankingAppCRUD.Application.Request.AccountRequest;
import com.example.BankingAppCRUD.Application.Response.AccountResponse;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Infrastructure.Service.Account.CheckingAccountServiceImpl;
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

    private CheckingAccountServiceImpl checkingAccountServiceImpl;


    @Autowired
    CheckingAccountController (CheckingAccountServiceImpl checkingAccountServiceImpl) {
        this.checkingAccountServiceImpl = checkingAccountServiceImpl;
    }




    @PostMapping
    public ResponseEntity<CheckingAccount> createAccount (AccountRequest checkingAccountRequest) {
        return new ResponseEntity<>(checkingAccountServiceImpl.createAccount(checkingAccountRequest) , HttpStatus.CREATED);
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
        return this.checkingAccountServiceImpl.getAllAccounts(pageable);
    }


    @GetMapping
    public ResponseEntity<Optional<AccountRequest>> getAccount (long Id) {

        return ResponseEntity.ok(checkingAccountServiceImpl.getAccountById(Id));
    }



    @PostMapping
    public ResponseEntity<AccountResponse> submitAccount (AccountRequest accDTO ) {


        checkingAccountServiceImpl.createAccount(AccountRequest);
    }


}

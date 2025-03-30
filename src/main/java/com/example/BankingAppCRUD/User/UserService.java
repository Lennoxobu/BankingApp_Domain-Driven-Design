package com.example.BankingAppCRUD.User;

import com.example.BankingAppCRUD.Account.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final CheckingAccountService checkingAccountService;
    private final SavingAccountService savingAccountService;


    @Autowired
    UserService (UserRepository userRepository, CheckingAccountService checkingAccountService, SavingAccountService savingAccountService) {
            this.userRepository = userRepository;
            this.checkingAccountService = checkingAccountService;
            this.savingAccountService = savingAccountService;
    }

    public boolean createUser (UserRequest userRequest) throws Exception {
        User user =  User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .checkingAccount(userRequest.getCheckingAccount())
                .savingAccount(userRequest.getSavingAccount())
                .build();

       if (this.userRepository.save(user) != user )
            throw new Exception();



       return true;
    }

    public boolean createUsers (List<UserRequest> users ) throws Exception{
        for (UserRequest user : users) {
            createUser(user);
        }
        return true;
    }

    public boolean  deleteUser ( Long ID ) {
        this.userRepository.deleteById(ID);

        return !this.userRepository.existsById(ID);


    }


    public void  deleteUsers (List<Long> Ids ) {
        for (Long id : Ids)
                this.userRepository.deleteById(id);

    }


    public Account  createCheckingAccount (AccountRequest checkingAccountRequest, Long Id) {

        return this.checkingAccountService.createAccount(checkingAccountRequest);


    }

    public Account  createSavingAccount (AccountRequest savingAccountRequest) {

        return this.savingAccountService.createAccount(savingAccountRequest);
    }


    public boolean deleteCheckingAccount (Long id ) {
            this.userRepository.getReferenceById(id).setCheckingAccount(null);

            return this.userRepository.getReferenceById(id).getCheckingAccount() == null;
    }


    public boolean deleteSavingAccount (Long id) {

        this.userRepository.getReferenceById(id).setCheckingAccount(null);

        return this.userRepository.getReferenceById(id).getCheckingAccount() == null;
    }













}

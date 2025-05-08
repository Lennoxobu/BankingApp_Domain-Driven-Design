package com.example.BankingAppCRUD.User;

import com.example.BankingAppCRUD.Account.Model.Account;
import com.example.BankingAppCRUD.Account.Model.AccountRequest;
import com.example.BankingAppCRUD.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Account.Service.CheckingAccountService;
import com.example.BankingAppCRUD.Account.Service.SavingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
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


    public Account createCheckingAccount (AccountRequest checkingAccountRequest, Long Id) {

        return this.checkingAccountService.createAccount(checkingAccountRequest);


    }

    public Account  createSavingAccount (AccountRequest savingAccountRequest) {

        return this.savingAccountService.createAccount(savingAccountRequest);
    }


    public boolean deleteCheckingAccount (Long id ) {

        CheckingAccount account = this.userRepository.getReferenceById(id).getCheckingAccount();


       if  (this.checkingAccountService.deleteAccount(account, account.getId())) {
           this.userRepository.removeCheckingAccount(id);

       } else { return false; };

       return true;

    }


    public boolean deleteSavingAccount (Long id) {

          SavingAccount account = this.userRepository.getReferenceById(id).getSavingAccount();



          if (this.savingAccountService.deleteAccount(account , account.getId())) {
              this.userRepository.removeSavingAccount(id);

          } else {

              return false;

          };



            return true;
    }













}

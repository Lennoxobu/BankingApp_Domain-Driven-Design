package com.example.BankingAppCRUD.Infrastructure.Service.User;

import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;
import com.example.BankingAppCRUD.Application.Request.AccountRequest;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.CheckingAccount;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.SavingAccount;
import com.example.BankingAppCRUD.Infrastructure.Service.Account.CheckingAccountServiceImpl;
import com.example.BankingAppCRUD.Infrastructure.Service.Account.SavingAccountServiceImpl;
import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import com.example.BankingAppCRUD.Application.Request.UserRequest;
import com.example.BankingAppCRUD.Infrastructure.Repository.User.UserJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl {


    private final UserJPARepository userJPARepository;
    private final CheckingAccountServiceImpl checkingAccountServiceImpl;
    private final SavingAccountServiceImpl savingAccountServiceImpl;


    @Autowired
    UserServiceImpl(UserJPARepository userJPARepository, CheckingAccountServiceImpl checkingAccountServiceImpl, SavingAccountServiceImpl savingAccountServiceImpl) {
            this.userJPARepository = userJPARepository;
            this.checkingAccountServiceImpl = checkingAccountServiceImpl;
            this.savingAccountServiceImpl = savingAccountServiceImpl;
    }

    public boolean createUser (UserRequest userRequest) throws Exception {
        User user =  User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .checkingAccount(userRequest.getCheckingAccount())
                .savingAccount(userRequest.getSavingAccount())
                .build();

       if (this.userJPARepository.save(user) != user )
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
        this.userJPARepository.deleteById(ID);

        return !this.userJPARepository.existsById(ID);


    }


    public void  deleteUsers (List<Long> Ids ) {
        for (Long id : Ids)
                this.userJPARepository.deleteById(id);

    }


    public Account createCheckingAccount (AccountRequest checkingAccountRequest, Long Id) {

        return this.checkingAccountServiceImpl.createAccount(checkingAccountRequest);


    }

    public Account  createSavingAccount (AccountRequest savingAccountRequest) {

        return this.savingAccountServiceImpl.createAccount(savingAccountRequest);
    }


    public boolean deleteCheckingAccount (Long id ) {

        CheckingAccount account = this.userJPARepository.getReferenceById(id).getCheckingAccount();


       if  (this.checkingAccountServiceImpl.deleteAccount(account, account.getId())) {
           this.userJPARepository.removeCheckingAccount(id);

       } else { return false; };

       return true;

    }


    public boolean deleteSavingAccount (Long id) {

          SavingAccount account = this.userJPARepository.getReferenceById(id).getSavingAccount();



          if (this.savingAccountServiceImpl.deleteAccount(account , account.getId())) {
              this.userJPARepository.removeSavingAccount(id);

          } else {

              return false;

          };



            return true;
    }













}

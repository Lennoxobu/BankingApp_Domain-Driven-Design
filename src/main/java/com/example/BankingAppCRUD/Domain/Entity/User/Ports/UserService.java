package com.example.BankingAppCRUD.Domain.Entity.User.Ports;

import com.example.BankingAppCRUD.Application.DTOs.Requests.Account.AccountDTO;
import com.example.BankingAppCRUD.Application.DTOs.Requests.User.UserDTO;
import com.example.BankingAppCRUD.Application.Response.Response;

import java.util.List;
import java.util.UUID;

public interface UserService {




    Response register (UserDTO user) throws Exception;

    Response createAccount (AccountDTO account ,  UUID userId ) throws Exception;

    Response deleteAccount (UUID id ,  UUID userId  );

    Response deleteUser (UUID id) throws Exception;

    Response changeEmail (String value , UUID id ) throws Exception;

    Response changeAddress (String value, UUID id ) throws Exception;

    Response changeName (UUID id , String firstName , String lastName ) throws Exception;

    List<AccountDTO>  getAccounts (UUID userId  ) throws Exception;

    Response setRole (String value , UUID id );

    Response changeStatus (UUID id, String value ) throws Exception;

//    AccountDTO getAccount(UUID accountId ,  UUID userId ) throws Exception;

}

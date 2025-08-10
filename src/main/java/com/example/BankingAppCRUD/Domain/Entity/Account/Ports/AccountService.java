package com.example.BankingAppCRUD.Domain.Entity.Account.Ports;

import com.example.BankingAppCRUD.Application.Response.Response;
import com.example.BankingAppCRUD.Domain.Entity.Account.Model.Account;

import java.util.UUID;


public interface AccountService <T extends Account>  {




    public Response viewBalance (UUID id ) throws Exception;

    public Response updateAccountStatus  (String selection ,  UUID id  ) throws Exception;

    public Response setRate (UUID id, double value  ) throws Exception;

    public Response  deposit (long value , UUID id ) throws Exception;

    public Response  withdraw (long  value , UUID id ) throws Exception;

    public Response  transfer (long  value , UUID receiverID,  UUID grantorID ) throws Exception;

    public Response applyRate (UUID id ) throws Exception;



}

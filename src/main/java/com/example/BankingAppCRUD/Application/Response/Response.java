package com.example.BankingAppCRUD.Application.Response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {

    private String responseCode;

    private String message;
}

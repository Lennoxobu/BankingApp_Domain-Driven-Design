package com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction;


import lombok.*;

import java.sql.Timestamp;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSortRequest {


    private Date date;
    private Long amount;
    private String direction;
}

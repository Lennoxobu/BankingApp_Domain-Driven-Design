package com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction;


import lombok.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterRequest {

    @NonNull
    private Date date;
    private Long amount;
    private UUID  sender_id;
    private UUID receiver_id;
}

package com.example.BankingAppCRUD.Infrastructure.Service.Transaction;

import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.TransactionFilterRequest;
import com.example.BankingAppCRUD.Application.DTOs.Requests.Transaction.TransactionPaginationRequest;
import com.example.BankingAppCRUD.Domain.Entity.Transaction.Model.FundTransaction;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class TransactionSpecification {

    public static Specification<FundTransaction> getSpecification (TransactionFilterRequest filterDto )  {
        return (root , query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterDto.getAmount() != null ) {
                predicates.add(criteriaBuilder.equal(root.get("amount"), filterDto));

            }

            if (filterDto.getDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("date"), filterDto));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}

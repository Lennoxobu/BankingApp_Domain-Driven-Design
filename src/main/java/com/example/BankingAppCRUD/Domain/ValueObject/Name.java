package com.example.BankingAppCRUD.Domain.ValueObject;


import com.fasterxml.jackson.databind.annotation.JsonAppend;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Embeddable
@AttributeOverrides({
        @AttributeOverride(name = "first" ,  column = @Column(name = "first_name" )),
        @AttributeOverride(name = "last" , column = @Column(name = "last_name")),
        @AttributeOverride(name = "knownAs" , column = @Column(name =  "knownAs_name"))
})
public class Name {


    private final String first;
    private final  String last;

    private final String knownAs;


}

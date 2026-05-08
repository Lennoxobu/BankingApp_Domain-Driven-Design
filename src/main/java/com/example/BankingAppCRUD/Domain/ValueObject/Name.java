package com.example.BankingAppCRUD.Domain.ValueObject;


import com.example.BankingAppCRUD.Domain.HibernateInstantiator.NameInstantiator;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.EmbeddableInstantiator;


@Data
@Builder
@Embeddable
@AllArgsConstructor
@AttributeOverrides({
        @AttributeOverride(name = "first" ,  column = @Column(name = "first_name" )),
        @AttributeOverride(name = "last" , column = @Column(name = "last_name")),
        @AttributeOverride(name = "knownAs" , column = @Column(name =  "knownAs_name"))
})
@EmbeddableInstantiator(NameInstantiator.class)
public class Name {


    private final String first;
    private final  String last;
    private final String knownAs;




}

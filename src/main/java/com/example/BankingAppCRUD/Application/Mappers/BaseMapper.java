package com.example.BankingAppCRUD.Application.Mappers;

import java.util.Collection;
import java.util.List;


public abstract class BaseMapper<E, D> {

        public abstract E convertToEntity(D dto, Object... args);

        public abstract D convertToDto(E entity, Object... args);

        public Collection<? extends E> convertToEntity(Collection<D> dto, Object... args) {
            return dto.stream().map(d -> convertToEntity(d, args)).toList();
        }

        public Collection<D> convertToDto(Collection<? extends E> entities, Object... args) {
            return entities.stream().map(entity -> convertToDto(entity, args)).toList();
        }

        public List<? extends E> convertToEntityList(Collection<D> dto, Object... args) {
            return convertToEntity(dto, args).stream().toList();
        }

        public List<D> convertToDtoList(Collection<? extends E> entities, Object... args) {
            return convertToDto(entities, args).stream().toList();
        }
    }


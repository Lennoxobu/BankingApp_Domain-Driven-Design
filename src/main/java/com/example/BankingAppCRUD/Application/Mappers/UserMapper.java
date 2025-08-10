package com.example.BankingAppCRUD.Application.Mappers;

import com.example.BankingAppCRUD.Application.DTOs.Requests.User.UserDTO;
import com.example.BankingAppCRUD.Domain.Entity.User.Model.User;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class UserMapper extends BaseMapper <User , UserDTO >{

    @Override
    public User convertToEntity(UserDTO dto, Object... args) {

        User user = User.builder().build();


        if(!Objects.isNull(dto)){
            BeanUtils.copyProperties(dto, user);
        }
        return user;

    }



    @Override
    public UserDTO convertToDto(User entity, Object... args) {

        UserDTO userDto = UserDTO.builder().build();
        if(!Objects.isNull(entity)) {
            BeanUtils.copyProperties(entity, userDto);
        }
        return userDto;
    }
}

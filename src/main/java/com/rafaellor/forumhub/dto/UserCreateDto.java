package com.rafaellor.forumhub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDto {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, message = "Username must have at least 3 characters")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;
}

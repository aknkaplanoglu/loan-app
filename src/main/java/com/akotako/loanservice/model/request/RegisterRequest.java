package com.akotako.loanservice.model.request;

import com.akotako.loanservice.model.Role;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role;
}

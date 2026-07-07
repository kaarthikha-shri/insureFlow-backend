package com.example.insureFlow.dto.response;

import com.example.insureFlow.entity.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponseDTO {

   
    private Long accountId;
    private String fullName;
    private UserRole role;

    public AuthResponseDTO( Long accountId, String fullName, UserRole role) {
        
        this.accountId = accountId;
        this.fullName = fullName;
        this.role = role;
    }
}
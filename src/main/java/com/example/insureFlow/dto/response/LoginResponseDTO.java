package com.example.insureFlow.dto.response;

import com.example.insureFlow.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    private Long accountId;
    private String fullName;
    private UserRole role;
}
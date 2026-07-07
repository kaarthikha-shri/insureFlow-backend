package com.example.insureFlow.controller;

import com.example.insureFlow.dto.request.LoginRequestDTO;
import com.example.insureFlow.dto.request.RegisterRequestDTO;
import com.example.insureFlow.dto.response.AuthResponseDTO;
import com.example.insureFlow.dto.response.LoginResponseDTO;
import com.example.insureFlow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // CHANGED: return type is now LoginResponseDTO (JSON: token, accountId, fullName, role)
    // instead of ResponseEntity<String>.
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/staff")
    @PreAuthorize("hasRole('INSURANCE_MANAGER')")
    public ResponseEntity<AuthResponseDTO> createStaffAccount(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createStaffAccount(request));
    }
}
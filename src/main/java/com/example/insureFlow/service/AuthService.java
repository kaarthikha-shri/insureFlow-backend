package com.example.insureFlow.service;

import com.example.insureFlow.dto.request.LoginRequestDTO;
import com.example.insureFlow.dto.request.RegisterRequestDTO;
import com.example.insureFlow.dto.response.AuthResponseDTO;
import com.example.insureFlow.dto.response.LoginResponseDTO;
import com.example.insureFlow.entity.SystemAccount;
import com.example.insureFlow.entity.UserRole;
import com.example.insureFlow.exception.BusinessValidationException;
import com.example.insureFlow.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SystemAccountRepository systemAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtUtil;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (request.getRole() != UserRole.POLICYHOLDER) {
            throw new BusinessValidationException(
                    "Self-registration is only allowed for the POLICYHOLDER role. " +
                    "Staff accounts must be created by an Insurance Manager.");
        }
        if (systemAccountRepository.existsByEmail(request.getEmail())) {
            throw new BusinessValidationException("An account with this email already exists.");
        }
        SystemAccount account = new SystemAccount();
        account.setEmail(request.getEmail());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setFullName(request.getFullName());
        account.setRole(UserRole.POLICYHOLDER);
        account.setIsActive(true);
        SystemAccount saved = systemAccountRepository.save(account);
        return new AuthResponseDTO(saved.getId(), saved.getFullName(), saved.getRole());
    }

    @Transactional
    public AuthResponseDTO createStaffAccount(RegisterRequestDTO request) {
        if (systemAccountRepository.existsByEmail(request.getEmail())) {
            throw new BusinessValidationException("An account with this email already exists.");
        }
        SystemAccount account = new SystemAccount();
        account.setEmail(request.getEmail());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setFullName(request.getFullName());
        account.setRole(request.getRole());
        account.setIsActive(true);
        SystemAccount saved = systemAccountRepository.save(account);
        return new AuthResponseDTO(saved.getId(), saved.getFullName(), saved.getRole());
    }

    // CHANGED: now returns LoginResponseDTO (token + accountId + fullName + role)
    // instead of a bare token string, so the frontend can store role/name
    // without decoding the JWT on the client.
    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BusinessValidationException("Invalid email or password.");
        }

        SystemAccount account = systemAccountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessValidationException("Invalid email or password."));

        if (!account.getIsActive()) {
            throw new BusinessValidationException("This account has been deactivated.");
        }

        String token = jwtUtil.generateToken(
                account.getEmail(), account.getRole().name(), account.getFullName(), account.getId());

        return LoginResponseDTO.builder()
                .token(token)
                .accountId(account.getId())
                .fullName(account.getFullName())
                .role(account.getRole())
                .build();
    }
}
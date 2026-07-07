package com.example.insureFlow.service;

import com.example.insureFlow.entity.SystemAccount;
import com.example.insureFlow.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SystemAccountRepository systemAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        SystemAccount account = systemAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No account found with email: " + email));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + account.getRole().name());

        return new User(
                account.getEmail(),
                account.getPasswordHash(),
                account.getIsActive(),
                true,
                true,
                true,
                Collections.singletonList(authority)
        );
    }
}
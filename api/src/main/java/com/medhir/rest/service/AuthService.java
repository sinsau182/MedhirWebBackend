package com.medhir.rest.service;

import com.medhir.rest.config.JwtUtil;
import com.medhir.rest.dto.AuthRequest;
import com.medhir.rest.dto.AuthResponse;
import com.medhir.rest.dto.RegisterRequest;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.model.Role;
import com.medhir.rest.model.UserAccount;
import com.medhir.rest.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String register(RegisterRequest request) {
        if (userAccountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException(
                    "User already exists with this email id");
        }

        UserAccount user = UserAccount.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(request.getRoles()) // Set<Role> directly stored
                .build();

        userAccountRepository.save(user);

        return "User registered successfully!";
    }

    public AuthResponse login(AuthRequest request) {
        Optional<UserAccount> userOpt = userAccountRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()
                || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            throw new BadCredentialsException("Invalid Email or Password");
        }

        UserAccount user = userOpt.get();
        Set<Role> roles = user.getRoles(); // Get roles directly as Set<Role>
        List<String> roleList = roles.stream().map(Enum::name).collect(Collectors.toList());

        String token = jwtUtil.generateToken(user.getEmail(), roles); // Pass Set<Role> directly

        return new AuthResponse(token, roleList);
    }
}

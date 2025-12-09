package com.casino.slot.service;

import com.casino.slot.entity.User;
import com.casino.slot.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceService {

    private final UserRepository userRepository;

    public BalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public BigDecimal getBalance(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + username));

        return user.getBalance();
    }
}
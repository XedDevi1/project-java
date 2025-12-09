package com.casino.slot.controller;

import com.casino.slot.entity.User;
import com.casino.slot.repository.UserRepository;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final UserRepository userRepository;

    public WalletController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record DepositRequest(
            @DecimalMin(value = "0.01", message = "Minimalna kwota wpłaty wynosi 0.01")
            BigDecimal amount
    ) {}

    public record BalanceResponse(BigDecimal balance) {}

    @PostMapping("/deposit")
    @Transactional
    public ResponseEntity<BalanceResponse> deposit(@RequestBody DepositRequest request, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono użytkownika"));

        user.setBalance(user.getBalance().add(request.amount()));
        userRepository.save(user);

        return ResponseEntity.ok(new BalanceResponse(user.getBalance()));
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(Principal principal) {
        BigDecimal balance = userRepository.findByUsername(principal.getName())
                .map(User::getBalance)
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono użytkownika"));

        return ResponseEntity.ok(new BalanceResponse(balance));
    }
}
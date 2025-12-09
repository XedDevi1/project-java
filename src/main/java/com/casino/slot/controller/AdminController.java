package com.casino.slot.controller;

import com.casino.slot.service.AdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    public record UpdatePayoutRequest(
            @NotNull Long slotId,
            @NotNull Long symbolId,
            @Min(3) Integer count,
            @NotNull @DecimalMin("0.01") BigDecimal newMultiplier
    ) {}

    @PostMapping("/update-payout")
    public ResponseEntity<?> updatePayout(@Valid @RequestBody UpdatePayoutRequest request) {
        try {
            adminService.updatePayoutMultiplier(
                    request.slotId(),
                    request.symbolId(),
                    request.count(),
                    request.newMultiplier()
            );

            return ResponseEntity.ok(Map.of("message", "Mnożnik wypłaty został pomyślnie zaktualizowany"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Nieoczekiwany błąd: " + e.getMessage()));
        }
    }
}
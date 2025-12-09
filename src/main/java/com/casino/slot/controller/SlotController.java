package com.casino.slot.controller;

import com.casino.slot.service.SlotService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    public record SpinRequest(
            @NotNull(message = "ID slotu jest wymagane")
            Long slotId,

            @NotNull(message = "Kwota zakładu jest wymagana")
            @DecimalMin(value = "0.01", message = "Kwota zakładu musi wynosić co najmniej 0.01")
            BigDecimal betAmount
    ) {}

    public record SpinResponse(
            Long spinId,
            Long slotId,
            BigDecimal betAmount,
            BigDecimal winAmount,
            Long[][] grid,
            List<SlotService.WinningLine> winningLines,
            LocalDateTime timestamp
    ) {}

    @PostMapping("/spin")
    public ResponseEntity<?> spin(@Valid @RequestBody SpinRequest request, Principal principal) {
        try {
            SlotService.SpinResult result = slotService.spin(
                    principal.getName(),
                    request.slotId(),
                    request.betAmount()
            );

            SpinResponse response = new SpinResponse(
                    result.spin.getId(),
                    result.spin.getSlot().getId(),
                    result.spin.getBetAmount(),
                    result.spin.getWinAmount(),
                    result.grid,
                    result.winningLines,
                    result.spin.getTimestamp()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Spin nieudany: " + e.getMessage()));
        }
    }
}
package com.casino.slot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "spins")
@Data
public class Spin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;

    private BigDecimal betAmount;
    private BigDecimal winAmount = BigDecimal.ZERO;

    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(columnDefinition = "JSON")
    private String resultGrid;        // например [["A","B","A"],["W","A","W"]]

    @Column(columnDefinition = "JSON")
    private String winningLines;      // список выигравших линий
}
package com.casino.slot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "slots")
@Data
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Integer rows = 3;
    private Integer columns = 5;

    private BigDecimal minBet = BigDecimal.valueOf(1);
    private BigDecimal maxBet = BigDecimal.valueOf(1000);

    private Double rtp = 96.5;
}
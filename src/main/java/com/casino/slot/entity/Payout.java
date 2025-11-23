package com.casino.slot.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "payouts")
@Data
public class Payout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;

    @ManyToOne
    @JoinColumn(name = "symbol_id")
    private Symbol symbol;

    private Integer count;      // 3, 4 или 5 одинаковых
    private BigDecimal multiplier; // например 10x ставка на линию
}
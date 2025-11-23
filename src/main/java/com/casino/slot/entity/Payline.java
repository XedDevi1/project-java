package com.casino.slot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "paylines")
@Data
public class Payline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    private String description; // например "Средняя горизонталь"

    @Column(columnDefinition = "TEXT")
    private String positions;   // JSON строка, например: "[[0,1],[1,1],[2,1],[3,1],[4,1]]"
}
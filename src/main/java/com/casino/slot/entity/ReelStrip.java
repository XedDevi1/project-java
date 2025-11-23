package com.casino.slot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reel_strips")
@Data
public class ReelStrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Slot slot;

    private Integer reelNumber; // 0..4

    @Column(columnDefinition = "TEXT")
    private String symbolsSequence; // например "A,B,C,A,Wild,A"
}
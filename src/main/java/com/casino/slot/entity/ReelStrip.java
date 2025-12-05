package com.casino.slot.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reel_strips")
public class ReelStrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Slot slot;

    private Integer reelNumber;

    @Column(columnDefinition = "TEXT")
    private String symbolsSequence;

    public ReelStrip() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Integer getReelNumber() {
        return reelNumber;
    }

    public void setReelNumber(Integer reelNumber) {
        this.reelNumber = reelNumber;
    }

    public String getSymbolsSequence() {
        return symbolsSequence;
    }

    public void setSymbolsSequence(String symbolsSequence) {
        this.symbolsSequence = symbolsSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReelStrip reelStrip = (ReelStrip) o;
        return Objects.equals(id, reelStrip.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ReelStrip{" +
                "id=" + id +
                ", reelNumber=" + reelNumber +
                '}';
    }
}
package com.casino.slot.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "paylines")
public class Payline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    private String description;

    @Column(columnDefinition = "TEXT")
    private String positions;

    public Payline() {}

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPositions() {
        return positions;
    }

    public void setPositions(String positions) {
        this.positions = positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payline payline = (Payline) o;
        return Objects.equals(id, payline.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Payline{" +
                "id=" + id +
                ", slot=" + (slot != null ? slot.getId() : "null") +
                ", description='" + description + '\'' +
                ", positions='" + positions + '\'' +
                '}';
    }
}
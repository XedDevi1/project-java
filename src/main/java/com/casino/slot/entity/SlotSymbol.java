package com.casino.slot.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "slot_symbols")
@IdClass(SlotSymbolId.class)
public class SlotSymbol {
    @Id
    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;

    @Id
    @ManyToOne
    @JoinColumn(name = "symbol_id")
    private Symbol symbol;

    private Integer weight = 1;

    public SlotSymbol() {}

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotSymbol that = (SlotSymbol) o;
        return Objects.equals(slot, that.slot) &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, symbol);
    }

    @Override
    public String toString() {
        return "SlotSymbol{" +
                "slot=" + (slot != null ? slot.getId() : null) +
                ", symbol=" + (symbol != null ? symbol.getName() : null) +
                ", weight=" + weight +
                '}';
    }
}
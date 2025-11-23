package com.casino.slot.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SlotSymbolId implements Serializable {

    private Long slot;
    private Long symbol;

    public SlotSymbolId() {}

    public SlotSymbolId(Long slot, Long symbol) {
        this.slot = slot;
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotSymbolId that = (SlotSymbolId) o;
        return Objects.equals(slot, that.slot) && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, symbol);
    }
}
package com.casino.slot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "slot_symbols")
@Data
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
}


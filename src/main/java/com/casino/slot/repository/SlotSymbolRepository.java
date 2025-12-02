package com.casino.slot.repository;

import com.casino.slot.entity.Slot;
import com.casino.slot.entity.SlotSymbol;
import com.casino.slot.entity.SlotSymbolId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotSymbolRepository extends JpaRepository<SlotSymbol, SlotSymbolId> {
    List<SlotSymbol> findBySlot(Slot slot);
}

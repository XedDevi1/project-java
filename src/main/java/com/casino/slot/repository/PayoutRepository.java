package com.casino.slot.repository;

import com.casino.slot.entity.Payout;
import com.casino.slot.entity.Slot;
import com.casino.slot.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayoutRepository extends JpaRepository<Payout, Long> {
    Optional<Payout> findBySlotAndSymbolAndCount(Slot slot, Symbol symbol, Integer count);
}

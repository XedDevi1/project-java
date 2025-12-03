package com.casino.slot.repository;

import com.casino.slot.entity.Payline;
import com.casino.slot.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaylineRepository extends JpaRepository<Payline, Long> {
    List<Payline> findBySlot(Slot slot);
}

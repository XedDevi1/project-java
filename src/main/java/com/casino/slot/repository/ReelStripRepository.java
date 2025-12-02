package com.casino.slot.repository;

import com.casino.slot.entity.ReelStrip;
import com.casino.slot.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReelStripRepository extends JpaRepository<ReelStrip, Long> {
    List<ReelStrip> findBySlot(Slot slot);
}

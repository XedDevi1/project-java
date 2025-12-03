package com.casino.slot.repository;

import com.casino.slot.entity.Spin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpinRepository extends JpaRepository<Spin, Long> {
}

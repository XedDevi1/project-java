package com.casino.slot.service;

import com.casino.slot.entity.Payout;
import com.casino.slot.entity.Slot;
import com.casino.slot.entity.Symbol;
import com.casino.slot.repository.PayoutRepository;
import com.casino.slot.repository.SlotRepository;
import com.casino.slot.repository.SymbolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AdminService {

    private final SlotRepository slotRepository;
    private final SymbolRepository symbolRepository;
    private final PayoutRepository payoutRepository;

    public AdminService(SlotRepository slotRepository,
                        SymbolRepository symbolRepository,
                        PayoutRepository payoutRepository) {
        this.slotRepository = slotRepository;
        this.symbolRepository = symbolRepository;
        this.payoutRepository = payoutRepository;
    }

    @Transactional
    public void updatePayoutMultiplier(Long slotId, Long symbolId, Integer count, BigDecimal newMultiplier) {
        if (newMultiplier == null || newMultiplier.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Mnożnik musi być większy od 0");
        }

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono slotu o ID: " + slotId));

        Symbol symbol = symbolRepository.findById(symbolId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono symbolu o ID: " + symbolId));

        if (count == null || count < 3 || count > slot.getColumns()) {
            throw new IllegalArgumentException("Liczba musi wynosić od 3 do " + slot.getColumns());
        }

        Payout payout = payoutRepository.findBySlotAndSymbolAndCount(slot, symbol, count)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono wypłaty dla podanych parametrów"));

        payout.setMultiplier(newMultiplier);
        payoutRepository.save(payout);
    }
}
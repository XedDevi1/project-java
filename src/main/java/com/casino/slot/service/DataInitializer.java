package com.casino.slot.service;

import com.casino.slot.entity.*;
import com.casino.slot.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SlotRepository slotRepository;
    private final SymbolRepository symbolRepository;
    private final SlotSymbolRepository slotSymbolRepository;
    private final PaylineRepository paylineRepository;
    private final PayoutRepository payoutRepository;
    private final ReelStripRepository reelStripRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           SlotRepository slotRepository,
                           SymbolRepository symbolRepository,
                           SlotSymbolRepository slotSymbolRepository,
                           PaylineRepository paylineRepository,
                           PayoutRepository payoutRepository,
                           ReelStripRepository reelStripRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.slotRepository = slotRepository;
        this.symbolRepository = symbolRepository;
        this.slotSymbolRepository = slotSymbolRepository;
        this.paylineRepository = paylineRepository;
        this.payoutRepository = payoutRepository;
        this.reelStripRepository = reelStripRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> init();
    }

    @Transactional
    public void init() throws Exception {
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(RoleName.USER);
                    return roleRepository.save(r);
                });

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(RoleName.ADMIN);
                    return roleRepository.save(r);
                });

        if (userRepository.findByUsername("player").isEmpty()) {
            User player = new User();
            player.setUsername("player");
            player.setPassword(passwordEncoder.encode("player"));
            player.setBalance(new BigDecimal("5000.00"));
            player.setRoles(Set.of(userRole));
            userRepository.save(player);
        }

        if (slotRepository.count() > 0) {
            return;
        }

        Symbol cherry = symbolRepository.save(newSymbol("Cherry", "🍒", 1));
        Symbol lemon  = symbolRepository.save(newSymbol("Lemon", "🍋", 2));
        Symbol seven  = symbolRepository.save(newSymbol("Seven", "7️⃣", 3));
        Symbol wild   = symbolRepository.save(newSymbol("Wild", "⭐", 10));

        createSimpleSlot("Fruit Cocktail", "Klasyczny automat owocowy",
                cherry, lemon, seven, wild);

        createSimpleSlot("Book of Ra", "Parodia slotu na książki",
                cherry, lemon, seven, wild);

        createSimpleSlot("Lucky Sevens", "Automat z naciskiem na siódemki",
                cherry, lemon, seven, wild);
    }

    private Symbol newSymbol(String name, String emoji, int value) {
        Symbol s = new Symbol();
        s.setName(name);
        s.setImageUrl(emoji);
        s.setValue(value);
        return s;
    }

    private void createSimpleSlot(String name,
                                  String description,
                                  Symbol cherry,
                                  Symbol lemon,
                                  Symbol seven,
                                  Symbol wild) throws Exception {
        Slot slot = new Slot();
        slot.setName(name);
        slot.setDescription(description);
        slot.setRows(3);
        slot.setColumns(5);
        slot.setMinBet(BigDecimal.ONE);
        slot.setMaxBet(new BigDecimal("100"));
        slot.setRtp(96.5);
        slot = slotRepository.save(slot);

        saveSlotSymbol(slot, cherry, 40);
        saveSlotSymbol(slot, lemon, 40);
        saveSlotSymbol(slot, seven, 15);
        saveSlotSymbol(slot, wild, 5);

        ReelStrip strip0 = new ReelStrip();
        strip0.setSlot(slot);
        strip0.setReelNumber(0);
        strip0.setSymbolsSequence("Cherry,Lemon,Seven,Wild,Cherry,Lemon");
        reelStripRepository.save(strip0);

        createHorizontalPayline(slot, 0, "Górna linia");
        createHorizontalPayline(slot, 1, "Linia środkowa");
        createHorizontalPayline(slot, 2, "Dolna linia");

        savePayout(slot, cherry, 3, new BigDecimal("5"));
        savePayout(slot, cherry, 4, new BigDecimal("10"));
        savePayout(slot, cherry, 5, new BigDecimal("20"));

        savePayout(slot, lemon, 3, new BigDecimal("4"));
        savePayout(slot, lemon, 4, new BigDecimal("8"));
        savePayout(slot, lemon, 5, new BigDecimal("16"));

        savePayout(slot, seven, 3, new BigDecimal("10"));
        savePayout(slot, seven, 4, new BigDecimal("25"));
        savePayout(slot, seven, 5, new BigDecimal("50"));
    }

    private void saveSlotSymbol(Slot slot, Symbol symbol, int weight) {
        SlotSymbol ss = new SlotSymbol();
        ss.setSlot(slot);
        ss.setSymbol(symbol);
        ss.setWeight(weight);
        slotSymbolRepository.save(ss);
    }

    private void createHorizontalPayline(Slot slot, int rowIndex, String description) throws Exception {
        int cols = slot.getColumns();

        List<List<Integer>> positions = java.util.stream.IntStream.range(0, cols)
                .mapToObj(col -> List.of(col, rowIndex))
                .toList();

        String json = objectMapper.writeValueAsString(positions);

        Payline line = new Payline();
        line.setSlot(slot);
        line.setDescription(description);
        line.setPositions(json);
        paylineRepository.save(line);
    }

    private void savePayout(Slot slot, Symbol symbol, int count, BigDecimal multiplier) {
        Payout p = new Payout();
        p.setSlot(slot);
        p.setSymbol(symbol);
        p.setCount(count);
        p.setMultiplier(multiplier);
        payoutRepository.save(p);
    }
}
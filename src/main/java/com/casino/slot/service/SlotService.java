package com.casino.slot.service;

import com.casino.slot.entity.*;
import com.casino.slot.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class SlotService {

    private final SlotRepository slotRepository;
    private final SpinRepository spinRepository;
    private final SlotSymbolRepository slotSymbolRepository;
    private final SymbolRepository symbolRepository;
    private final PaylineRepository paylineRepository;
    private final PayoutRepository payoutRepository;
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public SlotService(SlotRepository slotRepository,
                       SpinRepository spinRepository,
                       SlotSymbolRepository slotSymbolRepository,
                       SymbolRepository symbolRepository,
                       PaylineRepository paylineRepository,
                       PayoutRepository payoutRepository,
                       UserRepository userRepository) {
        this.slotRepository = slotRepository;
        this.spinRepository = spinRepository;
        this.slotSymbolRepository = slotSymbolRepository;
        this.symbolRepository = symbolRepository;
        this.paylineRepository = paylineRepository;
        this.payoutRepository = payoutRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SpinResult spin(String username, Long slotId, BigDecimal betAmount) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + username));

        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono slotu: " + slotId));

        validateBet(slot, user, betAmount);

        user.setBalance(user.getBalance().subtract(betAmount));
        userRepository.save(user);

        Long[][] grid = generateGrid(slot);
        List<WinningLine> winningLines = evaluateWins(slot, grid);

        BigDecimal winAmount = winningLines.stream()
                .map(wl -> betAmount.multiply(wl.multiplier))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (winAmount.compareTo(BigDecimal.ZERO) > 0) {
            user.setBalance(user.getBalance().add(winAmount));
            userRepository.save(user);
        }

        Spin savedSpin = saveSpinHistory(user, slot, betAmount, winAmount, grid, winningLines);

        return new SpinResult(savedSpin, grid, winningLines);
    }

    private Long[][] generateGrid(Slot slot) {
        List<SlotSymbol> symbols = slotSymbolRepository.findBySlot(slot);
        if (symbols.isEmpty()) {
            throw new IllegalStateException("Slot nie ma skonfigurowanych symboli");
        }

        int totalWeight = symbols.stream().mapToInt(SlotSymbol::getWeight).sum();
        Long[][] grid = new Long[slot.getRows()][slot.getColumns()];

        for (int r = 0; r < slot.getRows(); r++) {
            for (int c = 0; c < slot.getColumns(); c++) {
                grid[r][c] = getRandomSymbolId(symbols, totalWeight);
            }
        }
        return grid;
    }

    private Long getRandomSymbolId(List<SlotSymbol> symbols, int totalWeight) {
        int roll = random.nextInt(totalWeight);
        int currentWeight = 0;
        for (SlotSymbol ss : symbols) {
            currentWeight += ss.getWeight();
            if (roll < currentWeight) {
                return ss.getSymbol().getId();
            }
        }
        return symbols.get(0).getSymbol().getId();
    }

    private List<WinningLine> evaluateWins(Slot slot, Long[][] grid) {
        List<Payline> paylines = paylineRepository.findBySlot(slot);
        List<WinningLine> winningLines = new ArrayList<>();

        for (Payline payline : paylines) {
            List<Position> positions = parsePositions(payline.getPositions());
            if (positions.isEmpty()) continue;

            List<Long> lineSymbolIds = new ArrayList<>();
            boolean isLineValid = true;

            for (Position p : positions) {
                if (p.row >= slot.getRows() || p.col >= slot.getColumns()) {
                    isLineValid = false;
                    break;
                }
                lineSymbolIds.add(grid[p.row][p.col]);
            }

            if (!isLineValid || lineSymbolIds.isEmpty()) continue;

            Long firstSymbolId = lineSymbolIds.get(0);
            int matchCount = 1;

            for (int i = 1; i < lineSymbolIds.size(); i++) {
                if (lineSymbolIds.get(i).equals(firstSymbolId)) {
                    matchCount++;
                } else {
                    break;
                }
            }

            if (matchCount >= 3) {
                Symbol symbol = symbolRepository.findById(firstSymbolId).orElse(null);
                if (symbol != null) {
                    Payout payout = payoutRepository
                            .findBySlotAndSymbolAndCount(slot, symbol, matchCount)
                            .orElse(null);

                    if (payout != null) {
                        WinningLine wl = new WinningLine(
                                payline.getId(),
                                symbol.getId(),
                                symbol.getName(),
                                matchCount,
                                payout.getMultiplier()
                        );
                        for (int k = 0; k < matchCount; k++) {
                            wl.positions.add(positions.get(k));
                        }
                        winningLines.add(wl);
                    }
                }
            }
        }
        return winningLines;
    }

    private void validateBet(Slot slot, User user, BigDecimal betAmount) {
        if (betAmount.compareTo(slot.getMinBet()) < 0 || betAmount.compareTo(slot.getMaxBet()) > 0) {
            throw new IllegalArgumentException("Kwota zakładu jest poza dozwolonym zakresem");
        }
        if (user.getBalance().compareTo(betAmount) < 0) {
            throw new IllegalArgumentException("Niewystarczające środki na koncie");
        }
    }

    private Spin saveSpinHistory(User user, Slot slot, BigDecimal bet, BigDecimal win, Long[][] grid, List<WinningLine> lines) {
        Spin spin = new Spin();
        spin.setUser(user);
        spin.setSlot(slot);
        spin.setBetAmount(bet);
        spin.setWinAmount(win);
        try {
            spin.setResultGrid(objectMapper.writeValueAsString(grid));
            spin.setWinningLines(objectMapper.writeValueAsString(lines));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return spinRepository.save(spin);
    }

    private List<Position> parsePositions(String pos) {
        List<Position> list = new ArrayList<>();
        if (pos == null || pos.isBlank()) return list;

        try {
            if (pos.trim().startsWith("[[")) {
                int[][] arr = objectMapper.readValue(pos, int[][].class);
                for (int[] rc : arr) {
                    list.add(new Position(rc[0], rc[1]));
                }
            } else {
                for (String p : pos.split(";")) {
                    String[] parts = p.trim().split(",");
                    if (parts.length == 2) {
                        list.add(new Position(
                                Integer.parseInt(parts[0].trim()),
                                Integer.parseInt(parts[1].trim())
                        ));
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return list;
    }

    public static class SpinResult {
        public final Spin spin;
        public final Long[][] grid;
        public final List<WinningLine> winningLines;

        public SpinResult(Spin spin, Long[][] grid, List<WinningLine> winningLines) {
            this.spin = spin;
            this.grid = grid;
            this.winningLines = winningLines;
        }
    }

    public static class WinningLine {
        public Long lineId;
        public Long symbolId;
        public String symbolName;
        public int count;
        public BigDecimal multiplier;
        public final List<Position> positions = new ArrayList<>();

        public WinningLine(Long lineId, Long symbolId, String symbolName, int count, BigDecimal multiplier) {
            this.lineId = lineId;
            this.symbolId = symbolId;
            this.symbolName = symbolName;
            this.count = count;
            this.multiplier = multiplier;
        }
    }

    public static class Position {
        public int row;
        public int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}
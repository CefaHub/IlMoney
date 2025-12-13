package ru.illit.money;

import ru.illit.money.bridge.IllitAuctionBridge;

import java.util.*;
import java.util.stream.Collectors;

public final class TopCache {
    private final MoneyStore store;
    private final IllitAuctionBridge bridge; // may be null
    private List<UUID> top10 = List.of();

    public TopCache(MoneyStore store, IllitAuctionBridge bridge) {
        this.store = store;
        this.bridge = bridge;
        recalc();
    }

    public void recalc() {
        Map<UUID, Double> map = (bridge != null) ? bridge.allBalances() : store.allBalances();
        this.top10 = map.entrySet().stream()
                .sorted((a,b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<UUID> top10() { return top10; }
}

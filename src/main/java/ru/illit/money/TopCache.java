package ru.illit.money;

import java.util.*;

public final class TopCache {
    private final MoneyStore store;
    private List<UUID> top10 = List.of();

    public TopCache(MoneyStore store) { this.store = store; recalc(); }
    public void recalc() { this.top10 = store.top(10); }
    public List<UUID> top10() { return top10; }
}

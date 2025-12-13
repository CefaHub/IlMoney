package ru.illit.money.storage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BalanceStore {
    void reload();
    void save() throws IOException;

    double get(UUID id);
    void set(UUID id, double amount);

    void setName(UUID id, String name);
    String getName(UUID id);

    Map<UUID, Double> allBalances();
    List<UUID> top(int n);
}

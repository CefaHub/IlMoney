package ru.illit.money.storage;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BalanceStorage {
    double get(UUID id);
    void set(UUID id, double amount);

    void setName(UUID id, String name);
    String getName(UUID id);

    Map<UUID, Double> allBalances();
    List<UUID> top(int n);

    void reload();
    void save();
}

package ru.illit.money.storage;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class YamlBalanceStorage implements BalanceStorage {
    private final File file;
    private YamlConfiguration cfg;

    public YamlBalanceStorage(File file) {
        this.file = file;
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    @Override public void reload() { this.cfg = YamlConfiguration.loadConfiguration(file); }

    @Override public void save() {
        try { cfg.save(file); } catch (IOException ignored) {}
    }

    @Override public double get(UUID id) { return Math.max(0D, cfg.getDouble("balances." + id, 0D)); }

    @Override public void set(UUID id, double amount) { cfg.set("balances." + id, Math.max(0D, amount)); }

    @Override public void setName(UUID id, String name) {
        if (name != null) cfg.set("names." + id, name);
    }

    @Override public String getName(UUID id) { return cfg.getString("names." + id, ""); }

    @Override public Map<UUID, Double> allBalances() {
        Map<UUID, Double> out = new HashMap<>();
        var sec = cfg.getConfigurationSection("balances");
        if (sec == null) return out;
        for (String k : sec.getKeys(false)) {
            try {
                UUID id = UUID.fromString(k);
                out.put(id, Math.max(0D, sec.getDouble(k, 0D)));
            } catch (IllegalArgumentException ignored) {}
        }
        return out;
    }

    @Override public List<UUID> top(int n) {
        return allBalances().entrySet().stream()
                .sorted((a,b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}

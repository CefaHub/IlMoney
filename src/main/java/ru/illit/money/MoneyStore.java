package ru.illit.money;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class MoneyStore {
    private final File file;
    private YamlConfiguration cfg;

    public MoneyStore(File file) {
        this.file = file;
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() { this.cfg = YamlConfiguration.loadConfiguration(file); }
    public void save() throws IOException { cfg.save(file); }

    public double get(UUID id) { return Math.max(0, cfg.getDouble("balances." + id, 0D)); }
    public void set(UUID id, double amount) { cfg.set("balances." + id, Math.max(0, amount)); }

    public void setName(UUID id, String name) { if (name != null) cfg.set("names." + id, name); }
    public String getName(UUID id) { return cfg.getString("names." + id, ""); }

    public Map<UUID, Double> allBalances() {
        Map<UUID, Double> out = new HashMap<>();
        var sec = cfg.getConfigurationSection("balances");
        if (sec == null) return out;
        for (String key : sec.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                out.put(id, Math.max(0, sec.getDouble(key, 0D)));
            } catch (IllegalArgumentException ignored) {}
        }
        return out;
    }

    public List<UUID> top(int n) {
        return allBalances().entrySet().stream()
                .sorted((a,b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}

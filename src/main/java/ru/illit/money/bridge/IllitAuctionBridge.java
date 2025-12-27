package ru.illit.money.bridge;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Compatibility bridge: uses plugins/IllitAuction/balances.yml (balances.<uuid>) as the storage.
 * Note: IllitAuction keeps balances.yml in memory; IllitMoney writes to file and can also trigger reload in IllitAuction.
 */
public final class IllitAuctionBridge {
    private final File file;
    private YamlConfiguration cfg;

    public IllitAuctionBridge(File pluginsFolder) {
        this.file = new File(new File(pluginsFolder, "IllitAuction"), "balances.yml");
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public boolean exists() { return file.exists(); }

    public synchronized void reload() { this.cfg = YamlConfiguration.loadConfiguration(file); }

    public synchronized void save() throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        if (!file.exists()) file.createNewFile();
        cfg.save(file);
    }

    public synchronized double get(UUID id) {
        reload();
        return Math.max(0, cfg.getDouble("balances." + id, 0D));
    }

    public synchronized void set(UUID id, double amount) {
        reload();
        cfg.set("balances." + id, Math.max(0, amount));
        try { save(); } catch (Exception ignored) {}
    }

    public synchronized Map<UUID, Double> allBalances() {
        reload();
        Map<UUID, Double> out = new HashMap<>();
        ConfigurationSection sec = cfg.getConfigurationSection("balances");
        if (sec == null) return out;
        for (String key : sec.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                out.put(id, Math.max(0, sec.getDouble(key, 0D)));
            } catch (IllegalArgumentException ignored) {}
        }
        return out;
    }
}

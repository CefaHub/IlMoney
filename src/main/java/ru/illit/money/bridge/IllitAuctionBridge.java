package ru.illit.money.bridge;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Optional compatibility bridge: uses plugins/IllitAuction/balances.yml (balances.<uuid>) as the storage.
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
        cfg.set("balances." + id, Math.max(0, amount));
        try { save(); } catch (Exception ignored) {}
    }
}

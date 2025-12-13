package ru.illit.money.storage;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Совместимость с IllitAuction:
 * - Балансы хранятся в plugins/IllitAuction/balances.yml (ключ: balances.<uuid>)
 * - Имена хранятся отдельно в IllitMoney/names.yml
 */
public final class AuctionBalanceStore implements BalanceStore {
    private final File auctionBalancesFile;
    private final File namesFile;

    private YamlConfiguration balCfg;
    private YamlConfiguration namesCfg;

    public AuctionBalanceStore(File auctionPluginFolder, File illitMoneyFolder) {
        this.auctionBalancesFile = new File(auctionPluginFolder, "balances.yml");
        this.namesFile = new File(illitMoneyFolder, "names.yml");
        reload();
    }

    @Override
    public void reload() {
        this.balCfg = YamlConfiguration.loadConfiguration(auctionBalancesFile);
        this.namesCfg = YamlConfiguration.loadConfiguration(namesFile);
    }

    @Override
    public void save() throws IOException {
        if (!auctionBalancesFile.getParentFile().exists()) auctionBalancesFile.getParentFile().mkdirs();
        if (!auctionBalancesFile.exists()) auctionBalancesFile.createNewFile();
        if (!namesFile.getParentFile().exists()) namesFile.getParentFile().mkdirs();
        if (!namesFile.exists()) namesFile.createNewFile();

        balCfg.save(auctionBalancesFile);
        namesCfg.save(namesFile);
    }

    @Override
    public double get(UUID id) {
        return Math.max(0, balCfg.getDouble("balances." + id, 0D));
    }

    @Override
    public void set(UUID id, double amount) {
        balCfg.set("balances." + id, Math.max(0, amount));
    }

    @Override
    public void setName(UUID id, String name) {
        if (name != null) namesCfg.set("names." + id, name);
    }

    @Override
    public String getName(UUID id) {
        return namesCfg.getString("names." + id, "");
    }

    @Override
    public Map<UUID, Double> allBalances() {
        Map<UUID, Double> out = new HashMap<>();
        var sec = balCfg.getConfigurationSection("balances");
        if (sec == null) return out;
        for (String key : sec.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                out.put(id, Math.max(0, sec.getDouble(key, 0D)));
            } catch (IllegalArgumentException ignored) {}
        }
        return out;
    }

    @Override
    public List<UUID> top(int n) {
        return allBalances().entrySet().stream()
                .sorted((a,b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}

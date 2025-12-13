package ru.illit.money;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import ru.illit.money.api.IllitEconomy;
import ru.illit.money.bridge.IllitAuctionBridge;
import ru.illit.money.vault.VaultEconomyProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class IllitMoneyPlugin extends JavaPlugin {

    private MoneyStore store;
    private IllitEconomy economy;
    private TopCache top;

    private IllitAuctionBridge auctionBridge;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        File data = new File(getDataFolder(), "data.yml");
        if (!data.exists()) {
            try { data.createNewFile(); } catch (Exception ignored) {}
        }
        this.store = new MoneyStore(data);

        String currency = getConfig().getString("currency", "айлитики");
        boolean bridgeEnabled = getConfig().getBoolean("bridge.illitauction-balances", true);

        if (bridgeEnabled) {
            this.auctionBridge = new IllitAuctionBridge(getServer().getPluginsFolder());
            getLogger().info("Bridge IllitAuction balances.yml: ON");

            this.economy = new IllitEconomy() {
                private final IllitEconomyImpl base = new IllitEconomyImpl(store, currency);

                @Override public String currencyNamePlural() { return base.currencyNamePlural(); }
                @Override public String format(double amount) { return base.format(amount); }

                @Override public boolean has(java.util.UUID playerId, double amount) { return getBalance(playerId) >= amount; }

                @Override public double getBalance(java.util.UUID playerId) {
                    return auctionBridge.get(playerId);
                }

                @Override public void setBalance(java.util.UUID playerId, double amount) {
                    auctionBridge.set(playerId, amount);
                    pingIllitAuctionReload();
                }

                @Override public void add(java.util.UUID playerId, double amount) {
                    if (amount <= 0) return;
                    setBalance(playerId, getBalance(playerId) + amount);
                }

                @Override public boolean take(java.util.UUID playerId, double amount) {
                    amount = Math.max(0, amount);
                    double cur = getBalance(playerId);
                    if (cur < amount) return false;
                    setBalance(playerId, cur - amount);
                    return true;
                }
            };
        } else {
            this.economy = new IllitEconomyImpl(store, currency);
        }

        this.top = new TopCache(store, auctionBridge);

        Bukkit.getServicesManager().register(IllitEconomy.class, economy, this, ServicePriority.High);

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            Bukkit.getServicesManager().register(Economy.class, new VaultEconomyProvider(this, economy), this, ServicePriority.High);
            getLogger().info("Vault: Economy провайдер зарегистрирован.");
        } else {
            getLogger().warning("Vault не найден. Плагины через Vault не увидят айлитики.");
        }

        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);

        bind("pay", new PlayerCommands(economy, prefix()));
        bind("balance", new PlayerCommands(economy, prefix()));
        // aliases (bal/money) are in plugin.yml, no need separate bind()
        bind("baltop", new BalTopCommand(this, economy, top, prefix()));
        bind("illit", new IllitAdminCommand(economy, prefix()));

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new IllitMoneyPlaceholders(this, economy, top).register();
            getLogger().info("PlaceholderAPI: расширение Illit зарегистрировано.");
        }

        getLogger().info("IllitMoney включен.");
    }

    private void bind(String name, Object exec) {
        PluginCommand c = getCommand(name);
        if (c == null) return;
        if (exec instanceof org.bukkit.command.CommandExecutor ce) c.setExecutor(ce);
        if (exec instanceof org.bukkit.command.TabCompleter tc) c.setTabCompleter(tc);
    }

    /**
     * IllitAuction keeps balances.yml in memory (YamlConfiguration).
     * If IllitMoney writes directly to balances.yml, IllitAuction may still see old cached data.
     * This method tries to call YamlEconomy.reload() inside IllitAuction via reflection.
     */
    public void pingIllitAuctionReload() {
        try {
            var pl = Bukkit.getPluginManager().getPlugin("IllitAuction");
            if (pl == null) return;

            // find field named 'economy' inside IllitAuctionPlugin
            Field f = pl.getClass().getDeclaredField("economy");
            f.setAccessible(true);
            Object eco = f.get(pl);
            if (eco == null) return;

            Method m = eco.getClass().getMethod("reload");
            m.invoke(eco);
        } catch (NoSuchFieldException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (Throwable t) {
            // do not spam console
        }
    }

    public MoneyStore store() { return store; }

    public String prefix() { return getConfig().getString("prefix", "&7[&f&lILLIT &f&lMONEY&7]"); }
}

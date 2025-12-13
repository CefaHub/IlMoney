package ru.illit.money.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ru.illit.money.IllitMoneyPlugin;
import ru.illit.money.api.IllitEconomy;

import java.util.Collections;
import java.util.List;

/**
 * Vault Economy provider for IllitMoney.
 * Implements all methods used by Vault (including newer signature variants) with safe defaults for banks.
 */
public final class VaultEconomyProvider implements Economy {

    private final IllitMoneyPlugin plugin;
    private final IllitEconomy eco;

    public VaultEconomyProvider(IllitMoneyPlugin plugin, IllitEconomy eco) {
        this.plugin = plugin;
        this.eco = eco;
    }

    @Override public boolean isEnabled() { return plugin.isEnabled(); }
    @Override public String getName() { return "IllitMoney"; }

    @Override public String format(double amount) { return eco.format(amount); }
    @Override public String currencyNameSingular() { return eco.currencyNamePlural(); }
    @Override public String currencyNamePlural() { return eco.currencyNamePlural(); }

    @Override public int fractionalDigits() { return 0; }
    @Override public boolean hasBankSupport() { return false; }

    @Override public boolean hasAccount(String playerName) { return true; }
    @Override public boolean hasAccount(OfflinePlayer player) { return true; }
    @Override public boolean hasAccount(String playerName, String worldName) { return true; }
    @Override public boolean hasAccount(OfflinePlayer player, String worldName) { return true; }

    @Override public double getBalance(String playerName) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
        return eco.getBalance(p.getUniqueId());
    }
    @Override public double getBalance(OfflinePlayer player) { return eco.getBalance(player.getUniqueId()); }
    @Override public double getBalance(String playerName, String world) { return getBalance(playerName); }
    @Override public double getBalance(OfflinePlayer player, String world) { return getBalance(player); }

    @Override public boolean has(String playerName, double amount) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
        return eco.has(p.getUniqueId(), amount);
    }
    @Override public boolean has(OfflinePlayer player, double amount) { return eco.has(player.getUniqueId(), amount); }
    @Override public boolean has(String playerName, String world, double amount) { return has(playerName, amount); }
    @Override public boolean has(OfflinePlayer player, String world, double amount) { return has(player, amount); }

    @Override public EconomyResponse withdrawPlayer(String playerName, double amount) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
        return withdrawPlayer(p, amount);
    }
    @Override public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) amount = 0;
        boolean ok = eco.take(player.getUniqueId(), amount);
        return new EconomyResponse(amount, eco.getBalance(player.getUniqueId()),
                ok ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE,
                ok ? "" : "Недостаточно средств");
    }
    @Override public EconomyResponse withdrawPlayer(String playerName, String world, double amount) { return withdrawPlayer(playerName, amount); }
    @Override public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) { return withdrawPlayer(player, amount); }

    @Override public EconomyResponse depositPlayer(String playerName, double amount) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(playerName);
        return depositPlayer(p, amount);
    }
    @Override public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) amount = 0;
        eco.add(player.getUniqueId(), amount);
        return new EconomyResponse(amount, eco.getBalance(player.getUniqueId()), EconomyResponse.ResponseType.SUCCESS, "");
    }
    @Override public EconomyResponse depositPlayer(String playerName, String world, double amount) { return depositPlayer(playerName, amount); }
    @Override public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) { return depositPlayer(player, amount); }

    // Bank methods (not supported)
    @Override public EconomyResponse createBank(String name, String player) { return notSupported(); }
    @Override public EconomyResponse createBank(String name, OfflinePlayer player) { return notSupported(); }
    @Override public EconomyResponse deleteBank(String name) { return notSupported(); }
    @Override public EconomyResponse bankBalance(String name) { return notSupported(); }
    @Override public EconomyResponse bankHas(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse bankWithdraw(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse bankDeposit(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse isBankOwner(String name, String player) { return notSupported(); }
    @Override public EconomyResponse isBankOwner(String name, OfflinePlayer player) { return notSupported(); }
    @Override public EconomyResponse isBankMember(String name, String player) { return notSupported(); }
    @Override public EconomyResponse isBankMember(String name, OfflinePlayer player) { return notSupported(); }
    @Override public List<String> getBanks() { return Collections.emptyList(); }

    // Account creation (always true)
    @Override public boolean createPlayerAccount(String playerName) { return true; }
    @Override public boolean createPlayerAccount(OfflinePlayer player) { return true; }
    @Override public boolean createPlayerAccount(String playerName, String worldName) { return true; }
    @Override public boolean createPlayerAccount(OfflinePlayer player, String worldName) { return true; }

    private EconomyResponse notSupported() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Банки не поддерживаются");
    }
}

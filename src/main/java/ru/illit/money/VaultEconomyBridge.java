package ru.illit.money;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import ru.illit.money.api.IllitEconomy;

import java.util.List;

/**
 * Vault economy provider: makes IllitMoney compatible with any plugin that uses Vault.
 * Server must have Vault installed.
 */
public final class VaultEconomyBridge extends AbstractEconomy {

    private final IllitEconomy eco;

    public VaultEconomyBridge(IllitEconomy eco) {
        this.eco = eco;
    }

    @Override public boolean isEnabled() { return true; }
    @Override public String getName() { return "IllitMoney"; }
    @Override public String currencyNamePlural() { return eco.currencyNamePlural(); }
    @Override public String currencyNameSingular() { return eco.currencyNamePlural(); }
    @Override public int fractionalDigits() { return -1; } // supports decimals but we will format
    @Override public String format(double amount) { return eco.format(amount); }
    @Override public boolean hasAccount(OfflinePlayer player) { return player != null; }
    @Override public boolean hasAccount(String playerName) { return Bukkit.getOfflinePlayer(playerName) != null; }

    @Override public double getBalance(OfflinePlayer player) { return eco.getBalance(player.getUniqueId()); }
    @Override public double getBalance(String playerName) { return eco.getBalance(Bukkit.getOfflinePlayer(playerName).getUniqueId()); }

    @Override public boolean has(OfflinePlayer player, double amount) { return eco.has(player.getUniqueId(), amount); }
    @Override public boolean has(String playerName, double amount) { return eco.has(Bukkit.getOfflinePlayer(playerName).getUniqueId(), amount); }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "negative");
        boolean ok = eco.take(player.getUniqueId(), amount);
        return ok
                ? new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null)
                : new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "not enough");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "negative");
        eco.add(player.getUniqueId(), amount);
        return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
    }

    // Bank features: not supported
    @Override public boolean hasBankSupport() { return false; }
    @Override public EconomyResponse createBank(String name, String player) { return notSupported(); }
    @Override public EconomyResponse createBank(String name, OfflinePlayer player) { return notSupported(); }
    @Override public EconomyResponse deleteBank(String name) { return notSupported(); }
    @Override public EconomyResponse bankBalance(String name) { return notSupported(); }
    @Override public EconomyResponse bankHas(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse bankWithdraw(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse bankDeposit(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse isBankOwner(String name, String playerName) { return notSupported(); }
    @Override public EconomyResponse isBankOwner(String name, OfflinePlayer player) { return notSupported(); }
    @Override public EconomyResponse isBankMember(String name, String playerName) { return notSupported(); }
    @Override public EconomyResponse isBankMember(String name, OfflinePlayer player) { return notSupported(); }
    @Override public List<String> getBanks() { return List.of(); }

    @Override public boolean createPlayerAccount(OfflinePlayer player) { return true; }
    @Override public boolean createPlayerAccount(String playerName) { return true; }

    private EconomyResponse notSupported() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "bank not supported");
    }
}

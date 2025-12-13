package ru.illit.money.vault;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import ru.illit.money.IllitEconomyImpl;

public final class VaultEconomyBridge extends AbstractEconomy {
    private final IllitEconomyImpl eco;

    public VaultEconomyBridge(IllitEconomyImpl eco) {
        this.eco = eco;
    }

    @Override public boolean isEnabled() { return true; }
    @Override public String getName() { return "IllitMoney"; }
    @Override public boolean hasBankSupport() { return false; }
    @Override public int fractionalDigits() { return -1; }
    @Override public String format(double amount) { return eco.format(amount); }
    @Override public String currencyNamePlural() { return eco.currencyNamePlural(); }
    @Override public String currencyNameSingular() { return eco.currencyNamePlural(); }

    @Override public boolean hasAccount(OfflinePlayer player) { return player != null; }
    @Override public boolean hasAccount(String playerName) { return playerName != null && !playerName.isEmpty(); }
    @Override public boolean hasAccount(OfflinePlayer player, String worldName) { return hasAccount(player); }
    @Override public boolean hasAccount(String playerName, String worldName) { return hasAccount(playerName); }

    @Override public double getBalance(OfflinePlayer player) { return player == null ? 0D : eco.getBalance(player.getUniqueId()); }
    @Override public double getBalance(String playerName) { return 0D; } // Vault may call this; keep 0 to avoid name->uuid ambiguity
    @Override public double getBalance(OfflinePlayer player, String world) { return getBalance(player); }
    @Override public double getBalance(String playerName, String world) { return getBalance(playerName); }

    @Override public boolean has(OfflinePlayer player, double amount) { return player != null && eco.has(player.getUniqueId(), amount); }
    @Override public boolean has(String playerName, double amount) { return false; }
    @Override public boolean has(OfflinePlayer player, String world, double amount) { return has(player, amount); }
    @Override public boolean has(String playerName, String world, double amount) { return has(playerName, amount); }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (player == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "player_null");
        if (amount < 0) return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "amount_negative");
        boolean ok = eco.take(player.getUniqueId(), amount);
        return new EconomyResponse(amount, getBalance(player),
                ok ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE,
                ok ? null : "insufficient_funds");
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "use_uuid");
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (player == null) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "player_null");
        if (amount < 0) return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "amount_negative");
        eco.add(player.getUniqueId(), amount);
        return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "use_uuid");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override public boolean createPlayerAccount(OfflinePlayer player) { return true; }
    @Override public boolean createPlayerAccount(String playerName) { return true; }
    @Override public boolean createPlayerAccount(OfflinePlayer player, String worldName) { return true; }
    @Override public boolean createPlayerAccount(String playerName, String worldName) { return true; }
}

package ru.illit.money;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.illit.money.api.IllitEconomy;

import java.util.List;
import java.util.UUID;

public final class IllitMoneyPlaceholders extends PlaceholderExpansion {
    private final IllitMoneyPlugin plugin;
    private final IllitEconomy eco;
    private final TopCache top;

    public IllitMoneyPlaceholders(IllitMoneyPlugin plugin, IllitEconomy eco, TopCache top) {
        this.plugin = plugin;
        this.eco = eco;
        this.top = top;
    }

    @Override public @NotNull String getIdentifier() { return "Illit"; }
    @Override public @NotNull String getAuthor() { return "Illit"; }
    @Override public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
    @Override public boolean persist() { return true; }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("balance") || params.equalsIgnoreCase("bal")) {
            if (player == null) return "0";
            return eco.format(eco.getBalance(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("balance_raw")) {
            if (player == null) return "0";
            return String.valueOf(eco.getBalance(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("currency")) return eco.currencyNamePlural();

        if (params.toLowerCase().startsWith("baltop_")) {
            String tail = params.substring("baltop_".length());
            int idx;
            try { idx = Integer.parseInt(tail.split("_")[0]); } catch (Exception e) { return ""; }
            if (idx < 1 || idx > 10) return "";
            List<UUID> list = top.top10();
            if (list.size() < idx) return "";
            UUID id = list.get(idx - 1);
            String name = plugin.store().getName(id);
            if (name == null || name.isEmpty()) name = "Unknown";
            double bal = eco.getBalance(id);

            if (tail.contains("_name")) return name;
            if (tail.contains("_balance")) return eco.format(bal);
            return name + ": " + eco.format(bal);
        }
        return null;
    }
}

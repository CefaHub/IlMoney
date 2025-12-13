package ru.illit.money;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.illit.money.api.IllitEconomy;

import java.util.List;
import java.util.UUID;

public final class BalTopCommand implements CommandExecutor {
    private final IllitMoneyPlugin plugin;
    private final IllitEconomy eco;
    private final TopCache top;
    private final String prefix;

    public BalTopCommand(IllitMoneyPlugin plugin, IllitEconomy eco, TopCache top, String prefix) {
        this.plugin = plugin;
        this.eco = eco;
        this.top = top;
        this.prefix = prefix;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        top.recalc();
        List<UUID> list = top.top10();
        sender.sendMessage(U.c(prefix + " &7Топ-10 по балансу:"));
        for (int i = 0; i < 10; i++) {
            if (i >= list.size()) break;
            UUID id = list.get(i);
            String name = plugin.store().getName(id);
            if (name == null || name.isEmpty()) name = "Unknown";
            double bal = eco.getBalance(id);
            sender.sendMessage(U.c(prefix + " &f" + (i+1) + "&7) &f" + name + " &7- &f" + eco.format(bal) + " &7" + eco.currencyNamePlural()));
        }
        return true;
    }
}

package ru.illit.money;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.illit.money.api.IllitEconomy;

public final class PlayerCommands implements CommandExecutor, TabCompleter {
    private final IllitEconomy eco;
    private final String prefix;

    public PlayerCommands(IllitEconomy eco, String prefix) {
        this.eco = eco;
        this.prefix = prefix;
    }

    private double parseAmount(String s) {
        try { return Double.parseDouble(s.replace(",", ".")); } catch (Exception e) { return -1; }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("pay")) {
            if (!(sender instanceof Player p)) {
                sender.sendMessage(U.c(prefix + " &cКоманда доступна только игроку."));
                return true;
            }
            if (args.length < 2) {
                p.sendMessage(U.c(prefix + " &7Использование: &f/pay <ник> <сумма>"));
                return true;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.getUniqueId().equals(p.getUniqueId())) {
                p.sendMessage(U.c(prefix + " &cНельзя отправлять самому себе."));
                return true;
            }
            double amount = parseAmount(args[1]);
            if (amount <= 0) {
                p.sendMessage(U.c(prefix + " &cСумма должна быть больше 0."));
                return true;
            }
            if (!eco.take(p.getUniqueId(), amount)) {
                p.sendMessage(U.c(prefix + " &cНедостаточно средств."));
                return true;
            }
            eco.add(target.getUniqueId(), amount);

            p.sendMessage(U.c(prefix + " &7Вы отправили &f" + eco.format(amount) + " &7" + eco.currencyNamePlural() +
                    " игроку &f" + target.getName() + "&7."));
            if (target.isOnline()) {
                Player tp = target.getPlayer();
                if (tp != null) {
                    tp.sendMessage(U.c(prefix + " &7Вы получили &f" + eco.format(amount) + " &7" + eco.currencyNamePlural() +
                            " от игрока &f" + p.getName() + "&7."));
                }
            }
            return true;
        }

        if (cmd.equals("balance") || cmd.equals("bal") || cmd.equals("money")) {
            OfflinePlayer who;
            if (args.length >= 1) who = Bukkit.getOfflinePlayer(args[0]);
            else if (sender instanceof Player p) who = p;
            else {
                sender.sendMessage(U.c(prefix + " &7Использование: &f/balance <ник>"));
                return true;
            }
            sender.sendMessage(U.c(prefix + " &7Баланс игрока &f" + (who.getName() == null ? "Unknown" : who.getName()) +
                    "&7: &f" + eco.format(eco.getBalance(who.getUniqueId())) + " &7" + eco.currencyNamePlural()));
            return true;
        }

        return false;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String cmd = command.getName().toLowerCase();
        if ((cmd.equals("pay") || cmd.equals("balance") || cmd.equals("bal") || cmd.equals("money")) && args.length == 1) {
            return null;
        }
        return java.util.List.of();
    }
}

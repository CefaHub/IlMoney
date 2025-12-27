package ru.illit.money;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import ru.illit.money.api.IllitEconomy;

public final class IllitAdminCommand implements CommandExecutor, TabCompleter {
    private final IllitEconomy eco;
    private final String prefix;

    public IllitAdminCommand(IllitEconomy eco, String prefix) {
        this.eco = eco;
        this.prefix = prefix;
    }

    private double parseAmount(String s) {
        try { return Double.parseDouble(s.replace(",", ".")); } catch (Exception e) { return -1; }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("illit.money")) {
            sender.sendMessage(U.c(prefix + " &cНет прав."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(U.c(prefix + " &7Команды:"));
            sender.sendMessage(U.c(prefix + " &f/illit money set <ник> <сумма>"));
            sender.sendMessage(U.c(prefix + " &f/illit money add <ник> <сумма>"));
            sender.sendMessage(U.c(prefix + " &f/illit money take <ник> <сумма>"));
            sender.sendMessage(U.c(prefix + " &f/illit money reload"));
            return true;
        }

        if (!args[0].equalsIgnoreCase("money")) return false;

        if (args.length >= 2 && args[1].equalsIgnoreCase("reload")) {
            sender.sendMessage(U.c(prefix + " &aПерезагрузка выполняется через /reload или рестарт сервера (данные сохраняются автоматически)."));
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(U.c(prefix + " &7Использование: &f/illit money <set|add|take> <ник> <сумма>"));
            return true;
        }

        String action = args[1].toLowerCase();
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
        double amount = parseAmount(args[3]);
        if (amount < 0) {
            sender.sendMessage(U.c(prefix + " &cНекорректная сумма."));
            return true;
        }

        switch (action) {
            case "set" -> eco.setBalance(target.getUniqueId(), amount);
            case "add" -> eco.add(target.getUniqueId(), amount);
            case "take" -> {
                boolean ok = eco.take(target.getUniqueId(), amount);
                if (!ok) {
                    sender.sendMessage(U.c(prefix + " &cУ игрока недостаточно средств (баланс не изменён)."));
                    return true;
                }
            }
            default -> {
                sender.sendMessage(U.c(prefix + " &cНеизвестное действие. Используй set/add/take."));
                return true;
            }
        }

        sender.sendMessage(U.c(prefix + " &7Готово. Баланс &f" + (target.getName()==null?"Unknown":target.getName()) +
                "&7: &f" + eco.format(eco.getBalance(target.getUniqueId())) + " &7" + eco.currencyNamePlural()));
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return java.util.List.of("money");
        if (args.length == 2 && args[0].equalsIgnoreCase("money")) return java.util.List.of("set","add","take","reload");
        if (args.length == 3 && args[0].equalsIgnoreCase("money") && !args[1].equalsIgnoreCase("reload")) return null;
        return java.util.List.of();
    }
}

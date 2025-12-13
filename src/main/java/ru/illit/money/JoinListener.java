package ru.illit.money;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class JoinListener implements Listener {
    private final IllitMoneyPlugin plugin;
    public JoinListener(IllitMoneyPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.store().setName(e.getPlayer().getUniqueId(), e.getPlayer().getName());
        try { plugin.store().save(); } catch (Exception ignored) {}
    }
}

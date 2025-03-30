package org.holiday.simplePunishment.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.holiday.simplePunishment.Main;
import org.holiday.simplePunishment.Punishment;
import org.holiday.simplePunishment.enums.PunishmentType;
import org.holiday.simplePunishment.managers.PunishmentManager;

import java.util.List;
import java.util.UUID;

public class PreLoginListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerUUID = event.getUniqueId();
        List<Punishment> sanctions = PunishmentManager.getPunishments(playerUUID);
        for (Punishment p : sanctions) {
            if (p.isActive() &&
                    (p.getType() == PunishmentType.BAN ||
                            (p.getType() == PunishmentType.TEMP_BAN && !p.hasExpired()))) {
                String banMsg = Main.getInstance().getConfig().getString("messages.ban");
                banMsg = banMsg.replace("%reason%", p.getReason());
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.translateAlternateColorCodes('&', banMsg));
                return;
            }
        }
    }
}

package org.holiday.simplePunishment.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.holiday.simplePunishment.Punishment;
import org.holiday.simplePunishment.enums.PunishmentType;
import org.holiday.simplePunishment.managers.PunishmentManager;

import java.util.List;
import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        List<Punishment> punishments = PunishmentManager.getPunishments(playerUUID);

        for (Punishment p : punishments) {
            if (p.isActive() &&
                    (p.getType() == PunishmentType.MUTE ||
                            (p.getType() == PunishmentType.TEMP_MUTE && !p.hasExpired()))) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot speak because you are muted by: " + p.getReason());
                return;
            }
        }
    }
}

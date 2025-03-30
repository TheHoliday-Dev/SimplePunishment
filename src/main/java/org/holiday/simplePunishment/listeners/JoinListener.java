package org.holiday.simplePunishment.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.holiday.simplePunishment.Punishment;
import org.holiday.simplePunishment.enums.PunishmentType;
import org.holiday.simplePunishment.managers.PunishmentManager;

import java.util.List;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        List<Punishment> punishments = PunishmentManager.getPunishments(player.getUniqueId());
        for (Punishment p : punishments) {
            if (p.isActive() &&
                    (p.getType() == PunishmentType.BAN ||
                            (p.getType() == PunishmentType.TEMP_BAN && !p.hasExpired()))) {
                player.kickPlayer(ChatColor.RED + "You are banned for: " + p.getReason());
                return;
            }
        }

        for (Punishment p : PunishmentManager.getAllActivePunishments()) {
            if (p.getType() == PunishmentType.IP_BAN && p.getTargetIp() != null) {
                String playerIp = player.getAddress().getAddress().getHostAddress();
                if (playerIp.equals(p.getTargetIp())) {
                    player.kickPlayer(ChatColor.RED + "You are IP-banned for: " + p.getReason());
                    return;
                }
            }
        }
    }
}

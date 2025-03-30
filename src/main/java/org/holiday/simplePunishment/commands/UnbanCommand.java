package org.holiday.simplePunishment.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.holiday.simplePunishment.Main;
import org.holiday.simplePunishment.Punishment;
import org.holiday.simplePunishment.enums.PunishmentType;
import org.holiday.simplePunishment.managers.PunishmentManager;

import java.util.List;
import java.util.UUID;

public class UnbanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("punishment.unban")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /unban <player>");
            return true;
        }
        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || target.getName() == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + targetName);
            return true;
        }
        UUID targetUUID = target.getUniqueId();
        List<Punishment> punishments = PunishmentManager.getPunishments(targetUUID);
        boolean revoked = false;
        for (Punishment p : punishments) {
            if (p.isActive() && (p.getType() == PunishmentType.BAN ||
                    p.getType() == PunishmentType.TEMP_BAN ||
                    p.getType() == PunishmentType.IP_BAN)) {
                PunishmentManager.revokePunishment(p);
                revoked = true;
            }
        }
        if (revoked) {
            String unbanMsg = Main.getInstance().getConfig().getString("messages.unban");
            sender.sendMessage(ChatColor.GREEN + unbanMsg);
        } else {
            sender.sendMessage(ChatColor.RED + "Player " + target.getName() + " has no active ban/ipban punishments.");
        }
        return true;
    }
}

package org.holiday.simplePunishment.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.holiday.simplePunishment.Main;
import org.holiday.simplePunishment.Punishment;
import org.holiday.simplePunishment.enums.PunishmentType;
import org.holiday.simplePunishment.managers.PunishmentManager;

import java.time.LocalDateTime;
import java.util.UUID;

public class BanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("punishment.ban")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /ban <player> <reason>");
            return true;
        }
        String targetName = args[0];
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || target.getName() == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + targetName);
            return true;
        }
        UUID targetUUID = target.getUniqueId();
        UUID operatorUUID = (sender instanceof Player)
                ? ((Player) sender).getUniqueId()
                : new UUID(0, 0);

        Punishment punishment = new Punishment(
                targetUUID,
                PunishmentType.BAN,
                reason,
                operatorUUID,
                LocalDateTime.now(),
                0,
                true
        );

        PunishmentManager.addPunishment(punishment);
        String banMsg = Main.getInstance().getConfig().getString("messages.ban")
                .replace("%reason%", reason);
        sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been banned. " + banMsg);

        Player onlineTarget = Bukkit.getPlayer(targetUUID);
        if (onlineTarget != null) {
            onlineTarget.kickPlayer(ChatColor.translateAlternateColorCodes('&', banMsg));
        }
        return true;
    }
}

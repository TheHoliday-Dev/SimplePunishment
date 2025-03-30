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

public class TempBanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("punishment.tempban")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /tempban <player> <seconds> <reason>");
            return true;
        }
        String targetName = args[0];
        long duration;
        try {
            duration = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "The duration must be a number (in seconds).");
            return true;
        }
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
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
                PunishmentType.TEMP_BAN,
                reason,
                operatorUUID,
                LocalDateTime.now(),
                duration,
                true
        );
        PunishmentManager.addPunishment(punishment);

        LocalDateTime expiration = punishment.getExpirationDate();
        String tempBanMsg = Main.getInstance().getConfig().getString("messages.tempban")
                .replace("%reason%", reason)
                .replace("%expiration%", expiration.toString());
        sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been temporarily banned. " + tempBanMsg);

        Player onlineTarget = Bukkit.getPlayer(targetUUID);
        if (onlineTarget != null) {
            onlineTarget.kickPlayer(ChatColor.translateAlternateColorCodes('&', tempBanMsg));
        }
        return true;
    }
}

package org.holiday.simplePunishment.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class IPBanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("punishment.ipban")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /ipban <player> <reason>");
            return true;
        }

        String targetName = args[0];
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "The player must be online to apply an IP ban, as their IP is required.");
            return true;
        }

        String ip = target.getAddress().getAddress().getHostAddress();
        UUID targetUUID = target.getUniqueId();
        UUID operatorUUID = (sender instanceof Player)
                ? ((Player) sender).getUniqueId()
                : new UUID(0, 0);

        Punishment punishment = new Punishment(
                targetUUID,
                PunishmentType.IP_BAN,
                reason,
                operatorUUID,
                LocalDateTime.now(),
                0,
                true
        );
        punishment.setTargetIp(ip);

        PunishmentManager.addPunishment(punishment);
        String ipBanMsg = Main.getInstance().getConfig().getString("messages.ban")
                .replace("%reason%", reason);
        sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been IP-banned. " + ipBanMsg);

        target.kickPlayer(ChatColor.translateAlternateColorCodes('&', ipBanMsg));
        return true;
    }
}

package org.holiday.simplePunishment.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.holiday.simplePunishment.Main;
import org.holiday.simplePunishment.Punishment;
import org.holiday.simplePunishment.managers.PunishmentManager;

import java.util.List;
import java.util.UUID;

public class HistoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        UUID targetUUID = player.getUniqueId();
        if (args.length >= 1 && sender.hasPermission("punishment.history.others")) {
            targetUUID = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
        }
        List<Punishment> history = PunishmentManager.getPunishments(targetUUID);
        if (history.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No punishments were found for this player.");
            return true;
        }

        String title = ChatColor.translateAlternateColorCodes('&',
                Main.getInstance().getConfig().getString("historyMenu.title"));
        int size = Main.getInstance().getConfig().getInt("historyMenu.size");
        Inventory inv = Bukkit.createInventory(null, size, title);

        for (Punishment p : history) {
            ConfigurationSection itemConfig = Main.getInstance().getConfig().getConfigurationSection("historyMenu.items." + p.getType().name().toLowerCase());
            Material material = Material.matchMaterial(itemConfig.getString("material"));
            String itemName = ChatColor.translateAlternateColorCodes('&', itemConfig.getString("name"));

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(itemName);
            meta.setLore(List.of(
                    ChatColor.GRAY + "Reason: " + ChatColor.WHITE + p.getReason(),
                    ChatColor.GRAY + "Start Date: " + ChatColor.WHITE + p.getStartDate().toString(),
                    (p.getDuration() > 0
                            ? ChatColor.GRAY + "Expiration Date: " + ChatColor.WHITE + p.getExpirationDate().toString()
                            : ChatColor.GRAY + "Permanent")
            ));
            item.setItemMeta(meta);
            inv.addItem(item);
        }

        player.openInventory(inv);
        return true;
    }
}

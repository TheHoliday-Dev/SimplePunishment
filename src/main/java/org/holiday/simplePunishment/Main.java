package org.holiday.simplePunishment;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.holiday.simplePunishment.commands.*;
import org.holiday.simplePunishment.database.SQLiteHandler;
import org.holiday.simplePunishment.interfaces.DatabaseHandler;
import org.holiday.simplePunishment.listeners.ChatListener;
import org.holiday.simplePunishment.listeners.JoinListener;
import org.holiday.simplePunishment.listeners.PreLoginListener;
import org.holiday.simplePunishment.managers.PunishmentManager;

public final class Main extends JavaPlugin {
    private static Main main;
    private DatabaseHandler dbHandler;

    @Override
    public void onEnable() {
        main = this;
        saveDefaultConfig();

        dbHandler = new SQLiteHandler();
        dbHandler.connect();

        PunishmentManager.initialize(dbHandler);

        this.getCommand("ban").setExecutor(new BanCommand());
        this.getCommand("tempban").setExecutor(new TempBanCommand());
        this.getCommand("mute").setExecutor(new MuteCommand());
        this.getCommand("tempmute").setExecutor(new TempMuteCommand());
        this.getCommand("kick").setExecutor(new KickCommand());
        this.getCommand("unban").setExecutor(new UnbanCommand());
        this.getCommand("unmute").setExecutor(new UnmuteCommand());
        this.getCommand("ipban").setExecutor(new IPBanCommand());
        this.getCommand("history").setExecutor(new HistoryCommand());

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new PreLoginListener(), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                PunishmentManager.checkExpirations();
            }
        }.runTaskTimerAsynchronously(this, 20 * 60, 20 * 60); // Cada 60 segundos
    }

    @Override
    public void onDisable() {
        if (dbHandler != null && dbHandler.isConnected()) {
            dbHandler.disconnect();
        }
    }

    public static Main getInstance() {
        return main;
    }
}

package org.holiday.simplePunishment.managers;

import org.holiday.simplePunishment.Main;
import org.holiday.simplePunishment.Punishment;
import org.holiday.simplePunishment.interfaces.DatabaseHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PunishmentManager {
    private static List<Punishment> punishmentList = new ArrayList<>();
    private static HashMap<UUID, List<Punishment>> cache = new HashMap<>();
    private static DatabaseHandler databaseHandler;

    public static void initialize(DatabaseHandler handler) {
        databaseHandler = handler;
        punishmentList = databaseHandler.loadAllPunishments();
        for (Punishment p : punishmentList) {
            cache.computeIfAbsent(p.getPlayerUUID(), k -> new ArrayList<>()).add(p);
        }
    }

    public static void addPunishment(Punishment punishment) {
        punishmentList.add(punishment);
        cache.computeIfAbsent(punishment.getPlayerUUID(), k -> new ArrayList<>()).add(punishment);
        Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            databaseHandler.savePunishment(punishment);
        });
    }

    public static List<Punishment> getPunishments(UUID playerUUID) {
        return cache.getOrDefault(playerUUID, new ArrayList<>());
    }

    public static void updatePunishment(Punishment punishment) {
        Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            databaseHandler.updatePunishment(punishment);
        });
    }

    public static void revokePunishment(Punishment punishment) {
        punishment.setActive(false);
        updatePunishment(punishment);
    }

    public static void checkExpirations() {
        for (Punishment p : punishmentList) {
            if (p.isActive() && p.getDuration() > 0 && p.hasExpired()) {
                p.setActive(false);
                updatePunishment(p);
            }
        }
    }

    public static List<Punishment> getAllActivePunishments() {
        List<Punishment> active = new ArrayList<>();
        for (Punishment p : punishmentList) {
            if (p.isActive()) active.add(p);
        }
        return active;
    }
}

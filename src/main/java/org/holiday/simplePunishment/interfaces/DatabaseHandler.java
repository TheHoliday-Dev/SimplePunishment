package org.holiday.simplePunishment.interfaces;

import org.holiday.simplePunishment.Punishment;

import java.util.List;

public interface DatabaseHandler {
    void connect();

    void disconnect();

    boolean isConnected();

    void savePunishment(Punishment punishment);

    List<Punishment> loadAllPunishments();

    void updatePunishment(Punishment punishment);

    void deletePunishment(Punishment punishment);
}
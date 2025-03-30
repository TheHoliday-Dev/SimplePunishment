package org.holiday.simplePunishment.database;

import org.holiday.simplePunishment.Punishment;
import org.holiday.simplePunishment.enums.PunishmentType;
import org.holiday.simplePunishment.interfaces.DatabaseHandler;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLiteHandler implements DatabaseHandler {
    private Connection connection;
    private final String databaseFile = "plugins/SimplePunishment/banData.db";

    @Override
    public void connect() {
        try {
            File folder = new File("plugins/SimplePunishment");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Cargar el driver de SQLite
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS punishments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "playerUUID TEXT, " +
                    "type TEXT, " +
                    "reason TEXT, " +
                    "operatorUUID TEXT, " +
                    "date TEXT, " +
                    "duration INTEGER, " +
                    "active INTEGER, " +
                    "targetIp TEXT" +
                    ")");
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return (connection != null && !connection.isClosed());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void savePunishment(Punishment punishment) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO punishments (playerUUID, type, reason, operatorUUID, date, duration, active, targetIp) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, punishment.getPlayerUUID().toString());
            ps.setString(2, punishment.getType().name());
            ps.setString(3, punishment.getReason());
            ps.setString(4, punishment.getOperatorUUID().toString());
            ps.setString(5, punishment.getStartDate().toString());
            ps.setLong(6, punishment.getDuration());
            ps.setInt(7, punishment.isActive() ? 1 : 0);
            ps.setString(8, punishment.getTargetIp());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                punishment.setId(rs.getInt(1));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Punishment> loadAllPunishments() {
        List<Punishment> list = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM punishments");
            while (rs.next()) {
                int id = rs.getInt("id");
                UUID playerUUID = UUID.fromString(rs.getString("playerUUID"));
                String typeStr = rs.getString("type");
                String reason = rs.getString("reason");
                UUID operatorUUID = UUID.fromString(rs.getString("operatorUUID"));
                LocalDateTime date = LocalDateTime.parse(rs.getString("date"));
                long duration = rs.getLong("duration");
                boolean active = rs.getInt("active") == 1;
                String targetIp = rs.getString("targetIp");
                PunishmentType type = PunishmentType.valueOf(typeStr);
                Punishment punishment = new Punishment(playerUUID, type, reason, operatorUUID, date, duration, active);
                punishment.setId(id);
                punishment.setTargetIp(targetIp);
                list.add(punishment);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updatePunishment(Punishment punishment) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE punishments SET active = ?, targetIp = ? WHERE id = ?");
            ps.setInt(1, punishment.isActive() ? 1 : 0);
            ps.setString(2, punishment.getTargetIp());
            ps.setInt(3, punishment.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePunishment(Punishment punishment) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM punishments WHERE id = ?");
            ps.setInt(1, punishment.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package org.holiday.simplePunishment;

import org.holiday.simplePunishment.enums.PunishmentType;

import java.time.LocalDateTime;
import java.util.UUID;

public class Punishment {
    private int id;
    private UUID playerUUID;
    private PunishmentType type;
    private String reason;
    private UUID operatorUUID;
    private LocalDateTime startDate;
    private long duration; // Duración en segundos (0 = permanente)
    private boolean active;
    private String targetIp;

    public Punishment(UUID playerUUID, PunishmentType type, String reason,
                      UUID operatorUUID, LocalDateTime startDate, long duration, boolean active) {
        this.playerUUID = playerUUID;
        this.type = type;
        this.reason = reason;
        this.operatorUUID = operatorUUID;
        this.startDate = startDate;
        this.duration = duration;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public PunishmentType getType() {
        return type;
    }

    public void setType(PunishmentType type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public UUID getOperatorUUID() {
        return operatorUUID;
    }

    public void setOperatorUUID(UUID operatorUUID) {
        this.operatorUUID = operatorUUID;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public LocalDateTime getExpirationDate() {
        return (duration > 0) ? startDate.plusSeconds(duration) : null;
    }
    public boolean hasExpired() {
        LocalDateTime exp = getExpirationDate();
        return exp != null && LocalDateTime.now().isAfter(exp);
    }
}

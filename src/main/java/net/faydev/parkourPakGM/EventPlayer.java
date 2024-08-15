package net.faydev.parkourPakGM;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventPlayer {

    private final String playerName;
    private Player player;
    private Location checkpointLoc;
    private int checkpointStage;
    private final AtomicBoolean teleportingState = new AtomicBoolean(false);
    private final AtomicBoolean disableBowState = new AtomicBoolean(false);

    public EventPlayer(Player player, ParkourPakGM plugin) {
        this.playerName = player.getName();
        this.player = player;
        Object data = plugin.getData("location.spawn");
        if (data != null) {
            ConcurrentLinkedQueue<Location> list = (ConcurrentLinkedQueue<Location>) data;
            Location spawnLoc = list.stream().skip(new Random().nextInt(list.size())).findFirst().get();
            this.checkpointLoc = spawnLoc;
        } else {
            this.checkpointLoc = player.getLocation();
        }
        this.checkpointStage = 0;
        plugin.addEventPlayer(this);
    }

    public String getPlayerName() {
        return playerName;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Location getCheckpointLoc() {
        return checkpointLoc;
    }

    public void setCheckpointLoc(Location checkpointLoc) {
        this.checkpointLoc = checkpointLoc;
    }

    public int getCheckpointStage() {
        return checkpointStage;
    }

    public void setCheckpointStage(int checkpointStage) {
        this.checkpointStage = checkpointStage;
    }

    public AtomicBoolean getTeleportingState() {
        return this.teleportingState;
    }

    public AtomicBoolean getDisableBowState() {
        return disableBowState;
    }
}

package com.torchnight.sniperroyale.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public enum Config {
    INSTANCE;

    public World getWorld() {
        final String WORLD_NAME = "sniperrealm";
        return Bukkit.getWorld(WORLD_NAME);
    }

    public Location getRespawnPoint() {
        return new Location(getWorld(), 878.5, 170, -393.5);
    }

    public Location getSpawnPoint() {
        return new Location(getWorld(), 878.5, 169, -393.5);
    }

    public Location getEndGamePoint() {
        World spawnWorld = Bukkit.getWorld("torchnightbegin");
        return new Location (spawnWorld, 0.5, 135, 0.5);
    }

    public int getAutoStartPlayersNum() {
        return 4;
    }

}

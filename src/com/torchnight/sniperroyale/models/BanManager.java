package com.torchnight.sniperroyale.models;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum BanManager {

    INSTANCE;

    private Set<String> bannedUUIDsSet = new HashSet<>();
    private FileConfiguration config;

    public void initBanManager(FileConfiguration config) {
        this.config = config;
    }

    public void loadBans() {
        List<String> bannedUUIDList = config.getStringList("banned-uuids");
        bannedUUIDsSet.addAll(bannedUUIDList);
        Bukkit.getLogger().info("SniperRoyale: " + bannedUUIDList.size() + " ban(s) were loaded!");
    }

    public void saveBans() {
        int banListSize = bannedUUIDsSet.size();
        List<String> bannedUUIDList = Arrays.asList(bannedUUIDsSet.toArray(new String[banListSize]));
        config.set("banned-uuids", bannedUUIDList);
        Bukkit.getLogger().info("Saved " + bannedUUIDList.size() + " bans to SniperRoyale!");
    }

    public boolean checkForBan(String uuid) {
        return bannedUUIDsSet.contains(uuid);
    }

    public void banPlayer(String uuid) {
        bannedUUIDsSet.add(uuid);
    }

    public void unbanPlayer(String uuid) {
        bannedUUIDsSet.remove(uuid);
    }
}

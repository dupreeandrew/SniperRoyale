package com.torchnight.sniperroyale.models;

import org.bukkit.entity.Player;

import java.util.HashSet;

public enum PlayerLookup {
    INSTANCE;

    private HashSet<String> ingamePlayerNames = new HashSet<>();

    public void addPlayerToGameLookup(Player player) {
        ingamePlayerNames.add(player.getName());
    }

    public void removePlayerFromGameLookup(Player player) {
        ingamePlayerNames.remove(player.getName());
    }

    public boolean isPlayerIngame(Player player) {
        return ingamePlayerNames.contains(player.getName());
    }

}

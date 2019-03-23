package com.torchnight.sniperroyale.commands;

import com.torchnight.sniperroyale.models.GameQueue;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

class Queue {
    private Player p;

    Queue(Player p) {
        this.p = p;
    }

    void execute() {
        List<String> playerNames = GameQueue.INSTANCE.getPlayerNames();
        p.sendMessage(ChatColor.GREEN + "Queue:");

        for (String playerName : playerNames) {
            p.sendMessage(playerName);
        }
    }

}

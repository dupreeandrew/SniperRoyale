package com.torchnight.sniperroyale.commands;

import com.torchnight.sniperroyale.models.GameQueue;
import org.bukkit.entity.Player;

public class Start {

    private Player p;

    public Start(Player p) {
        this.p = p;
    }

    public void execute() {
        if (p.hasPermission("torchnight.eventmanager")) {
            if (GameQueue.INSTANCE.startGame()) {
                p.sendMessage("Game was started");
            }
            else {
                p.sendMessage("Game not could be started");
            }
        }
    }
}

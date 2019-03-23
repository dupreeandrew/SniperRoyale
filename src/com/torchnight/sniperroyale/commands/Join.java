package com.torchnight.sniperroyale.commands;

import com.torchnight.sniperroyale.models.BanManager;
import com.torchnight.sniperroyale.models.GameQueue;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


class Join {

    private Player player;

    Join(Player player) {
        this.player = player;
    }

    void execute() {

        if (BanManager.INSTANCE.checkForBan(player.getUniqueId().toString())) {
            final String FAIL_MESSAGE = ChatColor.RED + "You are banned from Sniper Royale";
            player.sendMessage(FAIL_MESSAGE);
            return;
        }

        if (!GameQueue.INSTANCE.addPlayer(player)) {
            final String FAIL_MESSAGE = ChatColor.RED + "Could not register for the queue. Are you already in?";
            player.sendMessage(FAIL_MESSAGE);
            return;
        }

        final String SUCCESS_MESSAGE = "Success! You entered the queue!";
        player.sendMessage(ChatColor.GREEN + SUCCESS_MESSAGE);

    }

}

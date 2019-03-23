package com.torchnight.sniperroyale.commands;

import com.torchnight.sniperroyale.models.BanManager;
import com.torchnight.sniperroyale.models.PermissionData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class Ban {
    static void banPlayer(Player commander, String bannableUUID) {
        if (commander.hasPermission(PermissionData.BAN)) {
            BanManager.INSTANCE.banPlayer(bannableUUID);
            commander.sendMessage(ChatColor.GREEN + "Player was banned successfully.");
        }
        else {
            commander.sendMessage(ChatColor.RED + "No permission");
        }
    }
}
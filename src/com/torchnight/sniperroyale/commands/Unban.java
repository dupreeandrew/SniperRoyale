package com.torchnight.sniperroyale.commands;

import com.torchnight.sniperroyale.models.BanManager;
import com.torchnight.sniperroyale.models.PermissionData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Unban {
    public static void unbanPlayer(Player commander, String unbannableUUID) {
        if (commander.hasPermission(PermissionData.UNBAN)) {
            BanManager.INSTANCE.unbanPlayer(unbannableUUID);
            commander.sendMessage(ChatColor.GREEN + "Player was unbanned successfully.");
        }
        else {
            commander.sendMessage(ChatColor.RED + "No permission");
        }
    }
}

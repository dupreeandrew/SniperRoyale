package com.torchnight.sniperroyale.listeners;

import com.torchnight.sniperroyale.models.GameQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameQueue.INSTANCE.removePlayerIfInQueue(player);
    }
}

package com.torchnight.sniperroyale.listeners;

import com.torchnight.sniperroyale.models.Config;
import com.torchnight.sniperroyale.models.PlayerLookup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class SneakListener implements Listener {

    private Config config;

    public SneakListener(Config config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        /*
        Player p = event.getPlayer();

        if (!PlayerLookup.INSTANCE.isPlayerIngame(p)) {
            return;
        }

        if (event.isSneaking()) {
            p.setWalkSpeed(config.getCrouchSpeedMultiplier());
        }
        else {
            p.setWalkSpeed(.2f);
        }
        */

    }

}

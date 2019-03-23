package com.torchnight.sniperroyale.listeners;

import com.torchnight.sniperroyale.models.Config;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GrenadeListener implements Listener {

    @EventHandler
    public void onGrenadeLaunched(PlayerTeleportEvent event) {


        if (conditionsAreMet(event)) {
            event.setCancelled(true);
            Location toSpawnTNTLocation = event.getTo();
            TNTPrimed tnt = Config.INSTANCE.getWorld().spawn(toSpawnTNTLocation, TNTPrimed.class);
            tnt.setFuseTicks(1);
        }

    }

    private boolean conditionsAreMet(PlayerTeleportEvent event) {
        Player p = event.getPlayer();
        if (!p.getWorld().equals(Config.INSTANCE.getWorld())) {
            return false;

        }
        return event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL;
    }

}

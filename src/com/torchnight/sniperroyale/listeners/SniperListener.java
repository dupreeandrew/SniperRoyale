package com.torchnight.sniperroyale.listeners;

import com.torchnight.sniperroyale.Main;
import com.torchnight.sniperroyale.models.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;

public class SniperListener implements Listener {
    private Main pl;
    private HashSet<Projectile> arrowEntitySet;
    private HashSet<Player> playerReloaders = new HashSet<>();

    public SniperListener(final Main pl) {
        this.arrowEntitySet = new HashSet<>();
        this.pl = pl;
    }

    @EventHandler
    public void onArrowShoot(final EntityShootBowEvent e) {



        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        final Player shooter = (Player) e.getEntity();
        if (isHoldingCorrectBow(shooter)) {

            if (playerReloaders.contains(shooter)) {
                shooter.sendMessage(ChatColor.RED + "You are currently reloading.");
                e.setCancelled(true);
                return;
            }


            Arrow arrowEntity = (Arrow) e.getProjectile();
            transformArrowToSniperBullet(arrowEntity);
            arrowEntitySet.add(arrowEntity);
            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(pl, new Runnable() {
                @Override
                public void run() {
                    arrowEntitySet.remove(arrowEntity);
                    arrowEntity.remove();
                }
            }, 140L);


            playerReloaders.add(shooter);
            Main.scheduleRepeatingTask(new Runnable() {

                private int ticksToFinish = 60;

                @Override
                public void run() {
                    DecimalFormat formatter = new DecimalFormat("##.00");
                    double secondsToFinish = ticksToFinish / 20.00;
                    shooter.sendMessage(ChatColor.GREEN + "Reloading.. " + formatter.format(secondsToFinish) + " more seconds");
                    ticksToFinish -= 5;

                    if (ticksToFinish <= 0) {
                        shooter.sendMessage(ChatColor.DARK_GREEN + "Reloaded weapon.");
                        playerReloaders.remove(shooter);
                        shooter.playSound(shooter.getLocation(), Sound.BLOCK_ANVIL_PLACE, 2.0F, 0.0F);
                    }

                }
            }, 5, 60);


            shooter.playSound(shooter.getLocation(), Sound.BLOCK_ANVIL_USE, 2.0F, 0.0F);
            Main.scheduleRepeatingTask(() ->
                    shooter.getWorld()
                            .playSound(shooter.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 1.0F), 1, 3);


        }
    }

    private void transformArrowToSniperBullet(Arrow arrowEntity) {
        arrowEntity.setVelocity(arrowEntity.getVelocity().multiply(9));
        arrowEntity.setKnockbackStrength(0);
        arrowEntity.setGravity(false);
    }

    private boolean isHoldingCorrectBow(Player player) {
        final String bowName = ChatColor.BLUE + "The Almighty Sniper";
        return player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(bowName);
    }
}


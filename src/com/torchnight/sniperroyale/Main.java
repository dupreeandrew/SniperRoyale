package com.torchnight.sniperroyale;

import com.torchnight.sniperroyale.commands.SniperRoyale;
import com.torchnight.sniperroyale.commands.iCommand;
import com.torchnight.sniperroyale.listeners.GrenadeListener;
import com.torchnight.sniperroyale.listeners.SneakListener;
import com.torchnight.sniperroyale.listeners.SniperListener;
import com.torchnight.sniperroyale.models.BanManager;
import com.torchnight.sniperroyale.models.Config;
import com.torchnight.sniperroyale.models.InventorySaver;
import com.torchnight.sniperroyale.models.PlayerLookup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        this.instance = this;
        getLogger().info("Sniper Royale has been enabled.");
        enableListeners();
        saveDefaultConfig();
        BanManager.INSTANCE.initBanManager(getConfig());
        BanManager.INSTANCE.loadBans();
    }

    @Override
    public void onDisable() {
        for (Player p: getServer().getOnlinePlayers()) {
            if (PlayerLookup.INSTANCE.isPlayerIngame(p)) {
                p.getInventory().clear();
                InventorySaver.INSTANCE.recoverInventory(p);
                p.teleport(Config.INSTANCE.getEndGamePoint());
            }
        }
        BanManager.INSTANCE.saveBans();
        saveConfig();
    }

    private void enableListeners() {
        Config config = Config.INSTANCE;
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new SneakListener(config), this);
        pm.registerEvents(new SniperListener(this), this);
        pm.registerEvents(new GrenadeListener(), this);
    }

    public static void registerListener(Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String commandLabel = command.getName().toLowerCase();
        iCommand executeableCommand;
        switch (commandLabel) {
            case "sniperroyale":
                executeableCommand = new SniperRoyale();
                break;
            default:
                return true;
        }

        return executeableCommand.onCommand(sender, command, args);

    }

    public static void broadcastMessage(String message) {
        String coloredMessage = ChatColor.translateAlternateColorCodes('&', message);
        Bukkit.broadcastMessage(coloredMessage);
    }

    public static void scheduleTask(Runnable runnable, long ticksInFuture) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, runnable, ticksInFuture);
    }

    public static void scheduleRepeatingTask(Runnable runnable, long ticksDelay, long ticksDuration) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        int id = scheduler.scheduleSyncRepeatingTask(instance, runnable, 0L, ticksDelay);
        scheduler.scheduleSyncDelayedTask(instance, new Runnable() {
            @Override
            public void run() {
                scheduler.cancelTask(id);
            }
        }, ticksDuration);
    }

    public static FileConfiguration getTheConfig() {
        return instance.getConfig();
    }

}

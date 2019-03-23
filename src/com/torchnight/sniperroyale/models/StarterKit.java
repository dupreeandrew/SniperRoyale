package com.torchnight.sniperroyale.models;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StarterKit {
    public static void give(Player p) {
        ItemFactory.giveSniper(p);
        ItemFactory.giveGrenades(p);
        p.getInventory().addItem(new ItemStack(Material.ELYTRA, 1));
        p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 32));
        p.getInventory().addItem(new ItemStack(Material.FIREWORK_ROCKET, 8));
        p.getInventory().addItem(new ItemStack(Material.ARROW, 512));

    }
}

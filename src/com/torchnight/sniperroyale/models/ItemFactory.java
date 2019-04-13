package com.torchnight.sniperroyale.models;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

class ItemFactory {
    static void giveSniper(Player p) {
        ItemStack sniperBow = new ItemStack(Material.BOW, 1);
        setSniperMeta(sniperBow);
        p.getInventory().addItem(sniperBow);
    }

    private static void setSniperMeta(ItemStack sniperBow) {
        ItemMeta sniperMeta = sniperBow.getItemMeta();

        final String NAME = ChatColor.RED + "The Almighty Sniper";
        final String[] loreLines = {
                "&6Legendary Item",
                "&e- Shoots 7x faster than a bow",
                "&d- Deals 7x more damage than a bow",
                "&e- Arrows do not have gravity"
        };

        sniperMeta.setDisplayName(NAME);
        sniperMeta.setLore(convertStringArrayToColoredList(loreLines));
        sniperBow.setItemMeta(sniperMeta);

    }

    private static List<String> convertStringArrayToColoredList(String[] lines) {

        List<String> coloredList = new ArrayList<>();

        for (String line : lines) {
            String coloredLine = ChatColor.translateAlternateColorCodes('&', line);
            coloredList.add(coloredLine);
        }

        return coloredList;

    }

    static void giveGrenades(Player p) {
        ItemStack grenades = new ItemStack(Material.ENDER_PEARL, 8);
        ItemMeta grenadeMeta = grenades.getItemMeta();
        grenadeMeta.setDisplayName(ChatColor.GREEN + "Grenade");
        grenades.setItemMeta(grenadeMeta);
        p.getInventory().addItem(grenades);
    }

}

package com.torchnight.sniperroyale.models;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public enum InventorySaver {
    INSTANCE;

    private Map<String, ItemStack[]> savedInventories = new HashMap<>();

    public void saveInventory(Player p) {
        ItemStack[] inventoryContents = p.getInventory().getContents();
        savedInventories.put(p.getName(), inventoryContents);
    }

    public boolean recoverInventory(Player p) {
        String playerName = p.getName();

        if (!savedInventories.containsKey(playerName)) {
            return false;
        }

        ItemStack[] savedInventoryContents = savedInventories.get(playerName);
        p.getInventory().setContents(savedInventoryContents);
        savedInventories.remove(playerName);
        return true;

    }

}

package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class ItemCraftListener implements Listener {

    @EventHandler
    public void onItemCraft(PrepareItemCraftEvent event) {
        final CraftingInventory inventory = event.getInventory();
        if (inventory.getResult() == null) return;

        for (ItemStack itemStack : inventory.getContents()) {
            if (Utils.getCrystals(itemStack) == 0) continue;
            inventory.setResult(null);
            return;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL && Utils.getCrystals(event.getCurrentItem()) != 0) {
            event.setCancelled(true);
        }
    }
}

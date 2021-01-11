package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Shop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashSet;
import java.util.stream.Collectors;

public class ShopMenuGUI extends GUI implements Listener {
    public MultiplePagesGUI<MultiplePagesGUI<ShopGUI>> pages;

    public ShopMenuGUI() {
        super(IridiumSkyblock.getInventories().shopGUISize, IridiumSkyblock.getInventories().shopGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);

        pages = new MultiplePagesGUI<>(() -> {
            for (Shop.ShopObject shopObject : IridiumSkyblock.getShop().shop) {
                setItem(shopObject.slot, Utils.makeItem(shopObject.display, 1, shopObject.displayName));
                pages.addPage(shopObject.slot, getMultiplePagesGUI(shopObject));
            }
        }, false);
    }

    private MultiplePagesGUI<ShopGUI> getMultiplePagesGUI(Shop.ShopObject shopObject) {
        return new MultiplePagesGUI<>(() -> {
            for (int page : shopObject.items.stream().map(item -> item.page).collect(Collectors.toCollection(HashSet::new))) {
                pages.getPage(shopObject.slot).addPage(page, new ShopGUI(shopObject, page));
            }
        }, false);
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            MultiplePagesGUI<ShopGUI> shopGUI = pages.getPage(e.getSlot());
            if (shopGUI.getPage(1) != null) { // This should always be called, but just incase the user configured the plugin incorrectly
                e.getWhoClicked().openInventory(shopGUI.getPage(1).getInventory());
            }
        }
    }
}
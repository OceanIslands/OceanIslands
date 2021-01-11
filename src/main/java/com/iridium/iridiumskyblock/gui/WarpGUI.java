package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WarpGUI extends GUI implements Listener {
    public Map<Integer, IslandWarp> warps = new HashMap<>();

    public WarpGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().warpGUISize, IridiumSkyblock.getInventories().warpGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland()!=null) {
            Island island = getIsland();
            int i = 9;
            warps.clear();
            for (IslandWarp islandWarp : island.islandWarps) {
                warps.put(i, islandWarp);
                setItem(i, Utils.makeItem(IridiumSkyblock.getInventories().islandWarp, Collections.singletonList(new Utils.Placeholder("warp", islandWarp.getName()))));
                i++;
            }
            if (IridiumSkyblock.getInventories().backButtons) setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            Player p = (Player) e.getWhoClicked();
            User u = User.getUser(p);
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                if (User.getUser((Player) e.getWhoClicked()).getIsland() != null) {
                    e.getWhoClicked().openInventory(User.getUser((Player) e.getWhoClicked()).getIsland().islandMenuGUI.getInventory());
                } else {
                    e.getWhoClicked().closeInventory();
                }
            }
            if (warps.containsKey(e.getSlot())) {
                IslandWarp islandWarp = warps.get(e.getSlot());
                if (e.getClick().equals(ClickType.RIGHT)) {
                    u.getIsland().removeWarp(warps.get(e.getSlot()));
                    getInventory().clear();
                    addContent();
                } else {
                    if (islandWarp.getPassword().isEmpty()) {
                        p.teleport(islandWarp.getLocation());
                        p.sendMessage(Utils.color(IridiumSkyblock.getMessages().teleporting.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    } else {
                        p.sendMessage(Utils.color(IridiumSkyblock.getMessages().enterPassword.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        u.islandWarp = islandWarp;
                    }
                }
                p.closeInventory();
            }
        }
    }
}

package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Boosters;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BoosterGUI extends GUI implements Listener {
    public BoosterGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().boosterGUISize, IridiumSkyblock.getInventories().boosterGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland() != null) {
            for (Boosters.Booster booster : IridiumSkyblock.getInstance().getIslandBoosters()) {
                if (booster.enabled) {
                    setItem(booster.item.slot, Utils.makeItem(booster.item, getIsland()));
                }
            }
            if (IridiumSkyblock.getInventories().backButtons)
                setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        }
    }

    public void sendMessage(Player p, String s) {
        for (String m : getIsland().members) {
            Player pl = Bukkit.getPlayer(User.getUser(m).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().activatedBooster.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix).replace("%player%", p.getName()).replace("%boostername%", s)));
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            Player p = (Player) e.getWhoClicked();
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
            }
            for (Boosters.Booster booster : IridiumSkyblock.getInstance().getIslandBoosters()) {
                if (booster.enabled && e.getSlot() == booster.item.slot) {
                    int time = getIsland().getBoosterTime(booster.name);
                    if (time == 0 || IridiumSkyblock.getConfiguration().stackableBoosters) {
                        Utils.BuyResponse response = Utils.canBuy(p, booster.vaultCost, booster.crystalsCost);
                        if (response == Utils.BuyResponse.SUCCESS) {
                            sendMessage(p, booster.name);
                            getIsland().addBoosterTime(booster.name, booster.time);
                        }
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().spawnerBoosterActive.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                }
            }
        }
    }
}
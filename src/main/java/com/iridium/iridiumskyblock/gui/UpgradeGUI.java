package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Upgrades;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class UpgradeGUI extends GUI implements Listener {

    public UpgradeGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().upgradeGUISize, IridiumSkyblock.getInventories().upgradeGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        Island island = getIsland();
        if (island != null) {
            for (Upgrades.Upgrade upgrade : IridiumSkyblock.getInstance().getIslandUpgrades()) {
                if (upgrade.enabled) {
                    setItem(upgrade.item.slot, Utils.makeItemHidden(upgrade.item, getIsland()));
                }
            }
            if (IridiumSkyblock.getInventories().backButtons)
                setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        }
    }

    private void sendMessage(Player p, String upgrade, int oldlvl, int newlvl) {
        for (String m : getIsland().members) {
            Player pl = Bukkit.getPlayer(User.getUser(m).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().islandUpgraded.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix).replace("%player%", p.getName()).replace("%upgradename%", upgrade).replace("%oldlvl%", oldlvl + "").replace("%newlvl%", newlvl + "")));
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            Player p = (Player) e.getWhoClicked();
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
            }
            for (Upgrades.Upgrade upgrade : IridiumSkyblock.getInstance().getIslandUpgrades()) {
                int level = getIsland().getUpgradeLevel(upgrade.name);
                if (e.getSlot() == upgrade.item.slot && upgrade.enabled) {
                    Upgrades.IslandUpgrade islandUpgrade = upgrade.getIslandUpgrade(level + 1);
                    if (islandUpgrade != null) {
                        Utils.BuyResponse response = Utils.canBuy(p, islandUpgrade.vaultCost, islandUpgrade.crystalsCost);
                        if (response == Utils.BuyResponse.SUCCESS) {
                            sendMessage(p, upgrade.name, level, level + 1);
                            getIsland().setUpgradeLevel(upgrade, level + 1);
                        } else {
                            p.sendMessage(Utils.color(response == Utils.BuyResponse.NOT_ENOUGH_VAULT ? IridiumSkyblock.getMessages().cantBuy : IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        }
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().maxLevelReached.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                    return;
                }
            }
        }
    }
}

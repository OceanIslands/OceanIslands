package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitGUI extends GUI implements Listener {
    
    private final int page;
    public Map<Integer, Integer> islands = new HashMap<>();

    public VisitGUI(int page) {
        super(IridiumSkyblock.getInventories().visitGUISize, IridiumSkyblock.getInventories().visitGUITitle, 40);
        IridiumSkyblock.getInstance().registerListeners(this);
        this.page = page;
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        List<Integer> islandIDS = IslandDataManager.getIslands(IslandDataManager.IslandSortType.VOTES, 45 * (page - 1), 45 * page, true);
        for (int i = 0; i < islandIDS.size(); i++) {
            Island island = IslandManager.getIslandViaId(islandIDS.get(i));
            if (island == null) continue;
            int slot = i;
            Bukkit.getScheduler().runTaskAsynchronously(IridiumSkyblock.getInstance(), () -> {
                User owner = User.getUser(island.owner);
                ItemStack head = Utils.makeItem(IridiumSkyblock.getInventories().visitisland, Arrays.asList(new Utils.Placeholder("player", owner.name), new Utils.Placeholder("name", island.getName()), new Utils.Placeholder("rank", Utils.getIslandRank(island) + ""), new Utils.Placeholder("votes", NumberFormat.getInstance().format(island.getVotes())), new Utils.Placeholder("value", island.getFormattedValue())));
                islands.put(slot, island.id);
                setItem(slot, head);
            });
        }
        setItem(getInventory().getSize() - 3, Utils.makeItem(IridiumSkyblock.getInventories().nextPage));
        setItem(getInventory().getSize() - 7, Utils.makeItem(IridiumSkyblock.getInventories().previousPage));
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (islands.containsKey(e.getSlot())) {
                Island island = IslandManager.getIslandViaId(islands.get(e.getSlot()));
                User u = User.getUser((OfflinePlayer) e.getWhoClicked());
                if (island.visit || u.bypassing) {
                    if (e.getClick() == ClickType.RIGHT) {
                        if (island.hasVoted(u)) {
                            island.removeVote(u);
                        } else {
                            island.addVote(u);
                        }
                    } else {
                        e.getWhoClicked().closeInventory();
                        island.teleportHome((Player) e.getWhoClicked());
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else if (e.getSlot() == getInventory().getSize() - 7) {
                VisitGUI visitGUI = IridiumSkyblock.getInstance().getVisitGUI().getPage(page - 1);
                if (visitGUI != null) {
                    visitGUI.addContent();
                    e.getWhoClicked().openInventory(visitGUI.getInventory());
                }
            } else if (e.getSlot() == getInventory().getSize() - 3) {
                VisitGUI visitGUI = IridiumSkyblock.getInstance().getVisitGUI().getPage(page + 1);
                if (visitGUI != null) {
                    visitGUI.addContent();
                    e.getWhoClicked().openInventory(visitGUI.getInventory());
                }
            }
        }
    }
}
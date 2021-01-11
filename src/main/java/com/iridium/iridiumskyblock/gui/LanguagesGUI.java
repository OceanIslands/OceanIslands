package com.iridium.iridiumskyblock.gui;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguagesGUI extends GUI implements Listener {

    private final int page;
    public Map<Integer, String> languages = new HashMap<>();

    public LanguagesGUI(int page, List<String> languages) {
        super(54, "&7Languages");
        this.page = page;
        int slot = 0;
        for (int i = 0; i < languages.size(); i++) {
            if (i >= (page - 1) * 45 && i < page * 54) {
                this.languages.put(slot, languages.get(i));
                slot++;
            }
        }
        Bukkit.getPluginManager().registerEvents(this, IridiumSkyblock.getInstance());
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        for (int slot : languages.keySet()) {
            setItem(slot, Utils.makeItem(XMaterial.PAPER, 1, "&b&l" + languages.get(slot)));
        }
        setItem(getInventory().getSize() - 3, Utils.makeItem(IridiumSkyblock.getInventories().nextPage));
        setItem(getInventory().getSize() - 7, Utils.makeItem(IridiumSkyblock.getInventories().previousPage));
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (languages.containsKey(e.getSlot())) {
                IridiumSkyblock.getInstance().setLanguage(languages.get(e.getSlot()), (Player) e.getWhoClicked());
            } else if (e.getSlot() == getInventory().getSize() - 7) {
                LanguagesGUI languagesGUI = IridiumSkyblock.getInstance().getLanguagesGUI().getPage(page - 1);
                if (languagesGUI != null) {
                    e.getWhoClicked().openInventory(languagesGUI.getInventory());
                }
            } else if (e.getSlot() == getInventory().getSize() - 3) {
                LanguagesGUI languagesGUI = IridiumSkyblock.getInstance().getLanguagesGUI().getPage(page + 1);
                if (languagesGUI != null) {
                    e.getWhoClicked().openInventory(languagesGUI.getInventory());
                }
            }
        }
    }
}

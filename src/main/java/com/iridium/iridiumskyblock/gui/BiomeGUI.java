package com.iridium.iridiumskyblock.gui;

import com.cryptomorin.xseries.XBiome;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BiomeGUI extends GUI implements Listener {

    public int page;
    public World.Environment environment;
    public Map<Integer, XBiome> biomes = new HashMap<>();

    public BiomeGUI(Island island, int page, World.Environment environment) {
        super(island, IridiumSkyblock.getInventories().biomeGUISize, IridiumSkyblock.getInventories().biomeGUITitle);
        this.page = page;
        this.environment = environment;
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        if (getInventory().getViewers().isEmpty()) return;
        super.addContent();
        int i = 0;
        int slot = 0;
        for (XBiome xBiome : IridiumSkyblock.getConfiguration().islandBiomes.keySet()) {
            if (xBiome.getEnvironment() != environment || xBiome.getBiome() == null) continue;
            if (i >= 45 * (page - 1) && slot < 45) {
                Config.BiomeConfig biomeConfig = IridiumSkyblock.getConfiguration().islandBiomes.get(xBiome);
                ItemStack itemStack = Utils.makeItem(IridiumSkyblock.getInventories().biome, Arrays.asList(
                        new Utils.Placeholder("price", Utils.NumberFormatter.format(biomeConfig.price)),
                        new Utils.Placeholder("crystals", Utils.NumberFormatter.format(biomeConfig.crystals)),
                        new Utils.Placeholder("biome", WordUtils.capitalize(xBiome.name().toLowerCase().replace("_", " ")))));
                Material icon = biomeConfig.icon.parseMaterial();
                if (icon != null) itemStack.setType(icon);
                setItem(slot, itemStack);
                biomes.put(slot, xBiome);
                slot++;
            }
            i++;
        }

        setItem(getInventory().getSize() - 3, Utils.makeItem(IridiumSkyblock.getInventories().nextPage));
        if (IridiumSkyblock.getInventories().backButtons)
            setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        setItem(getInventory().getSize() - 7, Utils.makeItem(IridiumSkyblock.getInventories().previousPage));
    }

    public void sendBiomeChangeMessage(String biome, Player p) {
        for (String member : getIsland().members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().biomeChanged
                        .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                        .replace("%biome%", biome).replace("%player%", p.getName())));
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (getInventory() != null) {
            if (e.getInventory().equals(getInventory())) {
                e.setCancelled(true);
                Player p = (Player) e.getWhoClicked();
                if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
                if (e.getSlot() == getInventory().getSize() - 3) {
                    BiomeGUI biomeGUI = environment == World.Environment.NORMAL ? getIsland().biomeGUI.getPage(page + 1) : getIsland().netherBiomeGUI.getPage(page + 1);
                    if (biomeGUI != null) {
                        p.openInventory(biomeGUI.getInventory());
                    }
                }
                if (e.getSlot() == getInventory().getSize() - 7) {
                    BiomeGUI biomeGUI = environment == World.Environment.NORMAL ? getIsland().biomeGUI.getPage(page - 1) : getIsland().netherBiomeGUI.getPage(page - 1);
                    if (biomeGUI != null) {
                        p.openInventory(biomeGUI.getInventory());
                    }
                }
                if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                    p.openInventory(getIsland().islandMenuGUI.getInventory());
                }
                if (biomes.containsKey(e.getSlot())) {
                    Config.BiomeConfig biomeConfig = IridiumSkyblock.getConfiguration().islandBiomes.get(biomes.get(e.getSlot()));
                    Utils.BuyResponse response = Utils.canBuy(p, IridiumSkyblock.getConfiguration().islandBiomes.getOrDefault(biomes.get(e.getSlot()), new Config.BiomeConfig()).price, biomeConfig.crystals);
                    if (response == Utils.BuyResponse.SUCCESS) {
                        switch (environment) {
                            case NORMAL:
                                getIsland().setBiome(biomes.get(e.getSlot()));
                                break;
                            case NETHER:
                                getIsland().setNetherBiome(biomes.get(e.getSlot()));
                        }
                        p.sendMessage(Utils.color(IridiumSkyblock.getMessages().biomePurchased
                                .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                                .replace("%biome%", WordUtils.capitalize(biomes.get(e.getSlot()).name().toLowerCase().replace("_", " ")))
                                .replace("%crystals%", biomeConfig.crystals + "")
                                .replace("%money", Utils.NumberFormatter.format(biomeConfig.price))));
                        sendBiomeChangeMessage(WordUtils.capitalize(biomes.get(e.getSlot()).name().toLowerCase().replace("_", " ")), p);
                    } else {
                        p.sendMessage(Utils.color((response == Utils.BuyResponse.NOT_ENOUGH_VAULT ? IridiumSkyblock.getMessages().cantBuy : IridiumSkyblock.getMessages().notEnoughCrystals).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                }
            }
        }
    }
}
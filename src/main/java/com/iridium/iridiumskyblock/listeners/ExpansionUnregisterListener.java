package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import me.clip.placeholderapi.events.ExpansionUnregisterEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ExpansionUnregisterListener implements Listener {

    @EventHandler
    public void onExpansionUnregister(ExpansionUnregisterEvent event) {
        if (!event.getExpansion().getIdentifier().equals("iridiumskyblock")) return;

        final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, plugin::setupClipsPlaceholderAPI);
    }

}

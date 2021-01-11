package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener {

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        final Config config = IridiumSkyblock.getConfiguration();
        if (config.denyNaturalSpawn.isEmpty() || event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL)
            return;

        final Entity entity = event.getEntity();
        final Location location = entity.getLocation();
        if (!IslandManager.isIslandWorld(location)) return;

        if (config.denyNaturalSpawnWhitelist != config.denyNaturalSpawn.contains(entity.getType())) {
            event.setCancelled(true);
        }
    }

}

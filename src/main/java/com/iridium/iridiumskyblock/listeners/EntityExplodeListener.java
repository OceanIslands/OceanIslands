package com.iridium.iridiumskyblock.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        final Entity entity = event.getEntity();
        final Location location = entity.getLocation();
        Island island = IslandManager.getIslandViaLocation(location);
        if (island == null) return;


        if (!IridiumSkyblock.getConfiguration().allowExplosions) {
            event.setCancelled(true);
            return;
        }
        Island fromIsland = IridiumSkyblock.getInstance().entities.get(entity.getUniqueId());
        if (fromIsland == null || !fromIsland.isInIsland(location)) {
            event.setCancelled(true);
            IridiumSkyblock.getInstance().entities.remove(entity.getUniqueId());
            return;
        }

        for (Block block : event.blockList()) {
            if (!fromIsland.isInIsland(location) || island.stackedBlocks.containsKey(block.getLocation())) {
                final BlockState state = block.getState();
                IridiumSkyblock.getNms().setBlockFast(block, 0, (byte) 0);
                Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), () -> state.update(true, true));
            } else {
                if (!Utils.isBlockValuable(block)) continue;

                if (!(block.getState() instanceof CreatureSpawner)) {
                    final Material material = block.getType();
                    final XMaterial xmaterial = XMaterial.matchXMaterial(material);
                    island.valuableBlocks.computeIfPresent(xmaterial.name(), (name, original) -> original - 1);
                }
            }
        }
        island.calculateIslandValue();
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        final Island island = IslandManager.getIslandViaLocation(event.getBlock().getLocation());
        for (Block block : event.blockList()) {
            final Location location = block.getLocation();
            if (!island.isInIsland(location)) continue;
            if (island.stackedBlocks.containsKey(location)) {
                BlockState state = block.getState();
                IridiumSkyblock.getNms().setBlockFast(block, 0, (byte) 0);
                Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), () -> state.update(true, false));
            }
        }
    }
}

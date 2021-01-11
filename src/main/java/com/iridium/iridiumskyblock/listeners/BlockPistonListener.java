package com.iridium.iridiumskyblock.listeners;

import com.google.common.collect.ImmutableMap;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.Map;

public class BlockPistonListener implements Listener {

    private static final Map<BlockFace, int[]> offsets = ImmutableMap.<BlockFace, int[]>builder()
            .put(BlockFace.EAST, new int[]{1, 0, 0})
            .put(BlockFace.WEST, new int[]{-1, 0, 0})
            .put(BlockFace.UP, new int[]{0, 1, 0})
            .put(BlockFace.DOWN, new int[]{0, -1, 0})
            .put(BlockFace.SOUTH, new int[]{0, 0, 1})
            .put(BlockFace.NORTH, new int[]{0, 0, -1})
            .build();

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
            final Block block = event.getBlock();
            final Location location = block.getLocation();
            final Island island = IslandManager.getIslandViaLocation(location);
            if (island == null) return;

            final BlockFace face = event.getDirection();
            for (Block extendedBlock : event.getBlocks()) {
                final Location extendedBlockLocation = extendedBlock.getLocation();
                final int[] offset = offsets.get(face);
                extendedBlockLocation.add(offset[0], offset[1], offset[2]);
                if (!island.isInIsland(extendedBlockLocation) || island.stackedBlocks.containsKey(extendedBlock.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event){
            final Block block = event.getBlock();
            final Location location = block.getLocation();
            final Island island = IslandManager.getIslandViaLocation(location);
            if (island == null) return;

            final BlockFace face = event.getDirection();
            for (Block extendedBlock : event.getBlocks()) {
                final Location extendedBlockLocation = extendedBlock.getLocation();
                final int[] offset = offsets.get(face);
                extendedBlockLocation.add(offset[0], offset[1], offset[2]);
                if (!island.isInIsland(extendedBlockLocation) || island.stackedBlocks.containsKey(extendedBlock.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
    }

    @EventHandler
    public void onBlockPistonReact(BlockPistonRetractEvent event) {
            final Block block = event.getBlock();
            final Location location = block.getLocation();
            final Island island = IslandManager.getIslandViaLocation(location);
            if (island == null) return;

            for (Block retractedBlock : event.getBlocks()) {
                final Location retractedBlockLocation = retractedBlock.getLocation();
                if (!island.isInIsland(retractedBlockLocation)) {
                    event.setCancelled(true);
                    return;
                }
            }
    }
}

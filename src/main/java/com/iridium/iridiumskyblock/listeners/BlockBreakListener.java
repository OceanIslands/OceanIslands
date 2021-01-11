package com.iridium.iridiumskyblock.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.*;
import com.iridium.iridiumskyblock.configs.Missions;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Crops;

import java.util.List;
import java.util.Map;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        final Block block = event.getBlock();
        final Location location = block.getLocation();
        final Island island = IslandManager.getIslandViaLocation(location);
        if (island == null) return;

        final Player player = event.getPlayer();
        final User user = User.getUser(player);

        if (user.islandID == island.id) {
            for (Missions.Mission mission : IridiumSkyblock.getMissions().missions) {
                final int key = island.getMissionLevels().computeIfAbsent(mission.name, (name) -> 1);
                final Map<Integer, Missions.MissionData> levels = mission.levels;
                final Missions.MissionData level = levels.get(key);

                if (level == null) continue;
                if (level.type != MissionType.BLOCK_BREAK) continue;

                final List<String> conditions = level.conditions;

                if (
                        conditions.isEmpty()
                                ||
                                conditions.contains(XMaterial.matchXMaterial(block.getType()).name())
                                ||
                                (
                                        block.getState().getData() instanceof Crops
                                                &&
                                                conditions.contains(((Crops) block.getState().getData()).getState().toString())
                                )
                )
                    island.addMission(mission.name, 1);
            }
        }

        if (!island.getPermissions(user).breakBlocks || (!island.getPermissions(user).breakSpawners && XMaterial.matchXMaterial(block.getType()).equals(XMaterial.SPAWNER))) {
            if (XMaterial.matchXMaterial(block.getType()).equals(XMaterial.SPAWNER)) {
                player.sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermissionBreakSpawners.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            } else {
                player.sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermissionBuild.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMonitorBreakBlock(BlockBreakEvent event) {
        final Block block = event.getBlock();
        final BlockState blockState = block.getState();
        final Location location = block.getLocation();
        final Island island = IslandManager.getIslandViaLocation(location);
        if (island == null) return;

        final XMaterial xmaterial = XMaterial.matchXMaterial(block.getType());
        if (Utils.isBlockValuable(block)) {
            island.valuableBlocks.computeIfPresent(xmaterial.name(), (name, original) -> original - 1);
            Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), island::calculateIslandValue);
        }

        island.failedGenerators.remove(location);

        if (island.stackedBlocks.containsKey(location)) {
            if (island.stackedBlocks.get(location) == 2) {
                island.stackedBlocks.remove(location);
            } else {
                island.stackedBlocks.compute(location, (loc, original) -> original - 1);
            }
            //This needs to be ran a tick later since blockbreakevent gets called before the block gets removed
            Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), () -> blockState.update(true, true));
            island.sendHolograms();
        }
    }
}

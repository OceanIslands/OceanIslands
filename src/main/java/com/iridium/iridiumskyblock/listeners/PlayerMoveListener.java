package com.iridium.iridiumskyblock.listeners;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final User user = User.getUser(player);
        final Island userIsland = user.getIsland();
        final Location location = player.getLocation();
        if (!IslandManager.isIslandWorld(location)) return;

        final Config config = IridiumSkyblock.getConfiguration();

        if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ() || event.getFrom().getY() != event.getTo().getY() && event.getTo().getY() < 0) {
            final Island island = IslandManager.getIslandViaLocation(location);

            if (island != null && !island.visit && !island.equals(userIsland) && !island.isCoop(userIsland) && !user.bypassing && !player.hasPermission("iridiumskyblock.visitbypass")) {
                island.spawnPlayer(event.getPlayer());
                return;
            }

            if (location.getY() < 0 && config.voidTeleport) {
                final World world = location.getWorld();
                if (world == null) return;

                if (island != null) {
                    if (!IridiumSkyblock.getConfiguration().keepInventoryOnVoid) player.getInventory().clear();
                    if (world.getName().equals(IslandManager.getWorld().getName()))
                        island.teleportHome(player);
                    else
                        island.teleportNetherHome(player);
                } else {
                    if (userIsland != null) {
                        if (world.getName().equals(IslandManager.getWorld().getName()))
                            userIsland.teleportHome(player);
                        else if (world.getName().equals(IslandManager.getNetherWorld().getName()))
                            userIsland.teleportNetherHome(player);
                    } else if (IslandManager.isIslandWorld(world)) {
                        if (Bukkit.getPluginManager().isPluginEnabled("EssentialsSpawn")) {
                            final PluginManager pluginManager = Bukkit.getPluginManager();
                            final EssentialsSpawn essentialsSpawn = (EssentialsSpawn) pluginManager.getPlugin("EssentialsSpawn");
                            final Essentials essentials = (Essentials) pluginManager.getPlugin("Essentials");
                            if (essentials != null && essentialsSpawn != null)
                                player.teleport(essentialsSpawn.getSpawn(essentials.getUser(player).getGroup()));
                        } else
                            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                    }
                }
            }
        }
        if (userIsland == null) return;

        if (user.flying
                && (!userIsland.isInIsland(location) || userIsland.getBoosterTime(IridiumSkyblock.getBoosters().islandFlightBooster.name) == 0)
                && !player.getGameMode().equals(GameMode.CREATIVE)
                && !(player.hasPermission("IridiumSkyblock.Fly")
                || player.hasPermission("iridiumskyblock.fly"))) {
            player.setAllowFlight(false);
            player.setFlying(false);
            user.flying = false;
            player.sendMessage(Utils.color(IridiumSkyblock.getMessages().flightDisabled
                    .replace("%prefix%", config.prefix)));
        }
    }
}

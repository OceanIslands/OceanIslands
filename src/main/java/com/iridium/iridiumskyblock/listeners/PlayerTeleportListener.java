package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.UUID;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
            final Location toLocation = event.getTo();
            final Location fromLocation = event.getFrom();
            if (!IslandManager.isIslandWorld(toLocation)) return;
            final Island toIsland = IslandManager.getIslandViaLocation(toLocation);
            if (toIsland == null) return;

            final Player player = event.getPlayer();
            Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), () -> toIsland.sendHolograms(player), 1);
            final User user = User.getUser(player);

            if (event.getCause().equals(TeleportCause.ENDER_PEARL)) {
                Island fromIsland = IslandManager.getIslandViaLocation(fromLocation);
                if (fromIsland == null || !fromIsland.isInIsland(toLocation)) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (user.islandID == toIsland.id) return;

            if ((toIsland.visit && !toIsland.isBanned(user)) || user.bypassing || player.hasPermission("iridiumskyblock.visitbypass")) {
                if (!toIsland.isInIsland(fromLocation)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), () -> toIsland.sendBorder(player), 1);
                    if (user.islandID != toIsland.id) {
                        player.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitingIsland.replace("%player%", User.getUser(toIsland.owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        if (player.hasPermission("iridiumskyblock.silentvisit")) return;
                        for (String pl : toIsland.members) {
                            Player p = Bukkit.getPlayer(UUID.fromString(pl));
                            if (p != null && p.canSee(player)) {
                                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitedYourIsland.replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            }
                        }
                    }
                }
            } else {
                event.setCancelled(true);
                player.sendMessage(Utils.color(IridiumSkyblock.getMessages().playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
    }
}

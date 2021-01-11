package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!IridiumSkyblock.getConfiguration().respawnAtIslandHome) return;
        Player player = event.getPlayer();
        User user = User.getUser(player);
        Island island = user.getIsland();
        if (island != null) {
            event.setRespawnLocation(island.home);
        }
    }
}

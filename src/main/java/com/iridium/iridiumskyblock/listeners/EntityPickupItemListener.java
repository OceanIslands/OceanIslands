package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class EntityPickupItemListener implements Listener {

    @EventHandler
    public void onEntityPickupItem(PlayerPickupItemEvent event) {
        final Item item = event.getItem();
        final Location location = item.getLocation();
        final Island island = IslandManager.getIslandViaLocation(location);
        if (island == null) return;

        final Player player = event.getPlayer();
        final User user = User.getUser(player);
        if (!island.getPermissions(user).pickupItems)
            event.setCancelled(true);
    }
}

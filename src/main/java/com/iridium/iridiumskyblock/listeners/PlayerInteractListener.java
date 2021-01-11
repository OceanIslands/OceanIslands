package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Location playerLocation = player.getLocation();
        if (!IslandManager.isIslandWorld(playerLocation)) return;

        final User user = User.getUser(player);
        final Block block = event.getClickedBlock();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (player.getItemInHand() != null) {
                int crystals = Utils.getCrystals(player.getItemInHand()) * player.getItemInHand().getAmount();
                if (crystals != 0) {
                    player.setItemInHand(null);
                    user.getIsland().setCrystals(user.getIsland().getCrystals() + crystals);
                    player.sendMessage(Utils.color(IridiumSkyblock.getMessages().depositedCrystals.replace("%amount%", crystals + "").replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
        }

        if (block != null) {
            final Location location = block.getLocation();
            final Island island = IslandManager.getIslandViaLocation(location);
            if (island != null) {
                if (!island.getPermissions(user).interact) {
                    event.setCancelled(true);
                    return;
                }
                final ItemStack itemInHand = player.getItemInHand();
                if (itemInHand.getType().equals(Material.BUCKET) && island.failedGenerators.remove(location)) {
                    if (itemInHand.getAmount() == 1)
                        itemInHand.setType(Material.LAVA_BUCKET);
                    else {
                        player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
                        player.getItemInHand().setAmount(itemInHand.getAmount() - 1);
                    }
                    block.setType(Material.AIR);
                }
            } else if (!user.bypassing) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        final Player player = event.getPlayer();
        final User user = User.getUser(player);
        final Entity rightClicked = event.getRightClicked();
        final Location location = rightClicked.getLocation();
        final Island island = IslandManager.getIslandViaLocation(location);
        if (island == null) return;
        if (!island.getPermissions(user).interact) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final User user = User.getUser(player);
        final Entity rightClicked = event.getRightClicked();
        final Location location = rightClicked.getLocation();
        final Island island = IslandManager.getIslandViaLocation(location);
        if (island == null) return;
        if (!island.getPermissions(user).interact) event.setCancelled(true);
    }
}

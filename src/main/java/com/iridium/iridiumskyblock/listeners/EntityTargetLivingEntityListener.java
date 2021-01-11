package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class EntityTargetLivingEntityListener implements Listener {

    @EventHandler
    public void onEntityTargetEntity(EntityTargetLivingEntityEvent event) {
        // Check if mobs should target island guests
        if (IridiumSkyblock.getConfiguration().allowMobGuestTargeting) {
            return;
        }

        // Check if the target is an entity and the targeting entity is a LivingEntity
        if (event.getTarget() == null || !(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        // Check if the targeted entity is a player
        if (event.getTarget().getType() != EntityType.PLAYER) {
            return;
        }

        // Check if the player is on an island
        Player targetedPlayer = (Player) event.getTarget();
        Island island = IslandManager.getIslandViaLocation(targetedPlayer.getLocation());
        if (island == null) {
            return;
        }

        // Check if this entity is allowed to target an entity
        LivingEntity entity = (LivingEntity) event.getEntity();

        // Check if the player is a guest
        User user = User.getUser(targetedPlayer);
        if (user.islandID != island.id) {
            // Cancel the event because the player is a guest
            event.setCancelled(true);

            // Try to find a new random target
            Set<Player> playersOnIsland = island.getPlayersOnIsland();
            if (playersOnIsland.size() != 1) {
                List<Player> possibleTargets = playersOnIsland.stream()
                        .filter(player -> player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR)
                        .filter(player -> User.getUser(player).islandID == island.id)
                        .filter(entity::hasLineOfSight)
                        .collect(Collectors.toList());

                // Return if there are none
                if (possibleTargets.size() == 0) {
                    return;
                }

                // Find and set a new random target
                Player nextTarget = possibleTargets.get(ThreadLocalRandom.current().nextInt(possibleTargets.size()));
                event.setTarget(nextTarget);
            }
        }
    }

}

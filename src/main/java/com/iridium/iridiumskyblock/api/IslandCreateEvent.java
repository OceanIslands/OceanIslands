package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class IslandCreateEvent extends IslandEvent {
    private final Player player;

    public IslandCreateEvent(@NotNull Player player, @NotNull Island island)  {
        super(island);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}

package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class IslandEvent extends Event {
    @NotNull
    private static final HandlerList handlers = new HandlerList();

    @NotNull private final Island island;

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
      return handlers;
    }

    public IslandEvent(@NotNull Island island) {
        this.island = island;
    }

    public Island getIsland() {
        return island;
    }
}

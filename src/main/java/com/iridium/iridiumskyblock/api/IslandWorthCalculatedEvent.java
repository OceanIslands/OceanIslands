package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import org.jetbrains.annotations.NotNull;

public class IslandWorthCalculatedEvent extends IslandEvent {

    private double islandWorth;

    public IslandWorthCalculatedEvent(@NotNull Island island, double islandWorth) {
        super(island);
        this.islandWorth = islandWorth;
    }

    public double getIslandWorth() {
        return islandWorth;
    }

    public void setIslandWorth(double islandWorth) {
        this.islandWorth = islandWorth;
    }
}

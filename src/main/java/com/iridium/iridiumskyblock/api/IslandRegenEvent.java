package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import org.jetbrains.annotations.NotNull;

public class IslandRegenEvent extends IslandEvent {
    public IslandRegenEvent(@NotNull Island island) {
        super(island);
    }
}

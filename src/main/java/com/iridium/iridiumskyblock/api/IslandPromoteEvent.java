package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class IslandPromoteEvent extends IslandEvent implements Cancellable {
    @NotNull private final User target;
    @NotNull private final User promoter;
    @NotNull private final Role role;
    private boolean cancelled;

    public IslandPromoteEvent(@NotNull Island island, @NotNull User target, @NotNull User promoter, @NotNull Role role) {
        super(island);
        this.target = target;
        this.promoter = promoter;
        this.role = role;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public User getTarget() {
        return target;
    }

    public User getPromoter() {
        return promoter;
    }

    public Role getRole() {
        return role;
    }
}

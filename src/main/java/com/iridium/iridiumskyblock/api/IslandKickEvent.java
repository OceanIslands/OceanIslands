package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IslandKickEvent extends IslandEvent implements Cancellable {
  @Nullable private final User userWhoKicked;
  @NotNull private final User kickedUser;
  private boolean cancelled = false;
  
  public IslandKickEvent(@NotNull Island island, @Nullable User userWhoKicked, @NotNull User kickedUser) {
    super(island);
    this.userWhoKicked = userWhoKicked;
    this.kickedUser = kickedUser;
  }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

  public User getUserWhoKicked() {
    return userWhoKicked;
  }

  public User getKickedUser() {
    return kickedUser;
  }
}

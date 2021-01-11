package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.MissionType;
import org.jetbrains.annotations.NotNull;

public class MissionCompleteEvent extends IslandEvent {
    @NotNull private final String missionName;
    @NotNull private final MissionType missionType;
    private final int missionLevel;

    public MissionCompleteEvent(@NotNull Island island, @NotNull String missionName, @NotNull MissionType missionType, int missionLevel) {
        super(island);
        this.missionName = missionName;
        this.missionType = missionType;
        this.missionLevel = missionLevel;
    }

    public String getMissionName() {
        return missionName;
    }

    public int getMissionLevel() {
        return missionLevel;
    }

    public MissionType getMissionType() {
        return missionType;
    }
}

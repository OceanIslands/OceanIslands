package com.iridium.iridiumskyblock;

import org.bukkit.Location;

public class IslandWarp {
    private final Location location;
    private final String name;
    private final String password;

    public IslandWarp(Location location, String name, String password) {
        this.location = location;
        this.name = name;
        this.password = password;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}

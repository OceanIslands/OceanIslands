package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MultiplePagesGUI<T> {
    private final Map<Integer, T> pages = new HashMap<>();

    public MultiplePagesGUI(Runnable addPages, boolean repeating) {
        if (repeating) {
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), addPages, 0, 20);
        } else {
            Bukkit.getScheduler().runTaskLater(IridiumSkyblock.getInstance(), addPages, 1);
        }
    }

    public T getPage(int i) {
        return pages.getOrDefault(i, null);
    }

    public void addPage(int i, T gui) {
        pages.put(i, gui);
    }

    public Collection<T> getAllPages() {
        return pages.values();
    }
}

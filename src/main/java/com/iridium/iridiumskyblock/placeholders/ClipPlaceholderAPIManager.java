package com.iridium.iridiumskyblock.placeholders;


import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Boosters;
import com.iridium.iridiumskyblock.configs.Upgrades;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ClipPlaceholderAPIManager extends PlaceholderExpansion {

    // Identifier for this expansion
    @Override
    public String getIdentifier() {
        return "iridiumskyblock";
    }

    @Override
    public String getAuthor() {
        return "Peaches_MLG";
    }

    // Since we are registering this expansion from the dependency, this can be null
    @Override
    public String getPlugin() {
        return null;
    }

    // Return the plugin version since this expansion is bundled with the dependency
    @Override
    public String getVersion() {
        return IridiumSkyblock.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String placeholder) {
        if (player == null || placeholder == null) {
            return "";
        }

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long time = (c.getTimeInMillis() - System.currentTimeMillis()) / 1000;
        int day = (int) TimeUnit.SECONDS.toDays(time);
        int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400L));
        int minute = (int) Math.floor((time - day * 86400 - hours * 3600) / 60.00);
        int second = (int) Math.floor((time - day * 86400 - hours * 3600) % 60.00);

        User user = User.getUser(player);

        switch (placeholder) {
            case "island_value":
                return user.getIsland() != null ? user.getIsland().getFormattedValue() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            case "island_level":
                return user.getIsland() != null ? user.getIsland().getFormattedLevel() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            case "island_rank":
                return user.getIsland() != null ? NumberFormat.getInstance().format(Utils.getIslandRank(user.getIsland())) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            case "island_owner":
                return user.getIsland() != null ? User.getUser(user.getIsland().owner).name : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            case "island_name":
                return user.getIsland() != null ? user.getIsland().getName() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            case "island_crystals":
                return user.getIsland() != null ? user.getIsland().getFormattedCrystals() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            case "island_members":
                return user.getIsland() != null ? String.valueOf(user.getIsland().members.size()) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            case "island_members_online":
                if (user.getIsland() == null) return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
                int online = 0;
                for (String member : user.getIsland().members) {
                    if (Bukkit.getPlayer(User.getUser(member).name) != null) {
                        online++;
                    }
                }
                return String.valueOf(online);
            case "island_bank_vault":
                return user.getIsland() != null ? user.getIsland().getFormattedMoney() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            case "island_bank_experience":
                return user.getIsland() != null ? user.getIsland().getFormattedExp() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            case "island_biome":
                return user.getIsland() != null ? user.getIsland().biome.name() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            case "midnight_seconds":
                return String.valueOf(second);
            case "midnight_minutes":
                return String.valueOf(minute);
            case "midnight_hours":
                return String.valueOf(hours);
            case "island_role":
                return user.getIsland() != null ? user.getRole().toString() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        }
        for (Upgrades.Upgrade upgrade : IridiumSkyblock.getInstance().getIslandUpgrades()) {
            int level = user.getIsland() != null ? user.getIsland().getUpgradeLevel(upgrade.name) : 1;
            Upgrades.IslandUpgrade islandUpgrade = upgrade.getIslandUpgrade(level);
            if (placeholder.equals("island_upgrade_" + upgrade.name + "_level")) {
                return user.getIsland() != null ? NumberFormat.getInstance().format(level) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            if (placeholder.equals("island_upgrade_" + upgrade.name + "_dimensions") && islandUpgrade.size != null) {
                return user.getIsland() != null ? Integer.toString(islandUpgrade.size) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            if (placeholder.equals("island_upgrade_" + upgrade.name + "_amount") && islandUpgrade.size != null) {
                return user.getIsland() != null ? Integer.toString(islandUpgrade.size) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            if (placeholder.equals("island_upgrade_" + upgrade.name + "_count") && islandUpgrade.size != null) {
                return user.getIsland() != null ? Integer.toString(islandUpgrade.size) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
        }
        for (Boosters.Booster booster : IridiumSkyblock.getInstance().getIslandBoosters()) {
            if (placeholder.equals("island_booster_" + booster.name)) {
                return user.getIsland() != null ? Integer.toString(user.getIsland().getBoosterTime(booster.name)) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
        }
        if (placeholder.startsWith("island_top_name_")) {
            try {
                int integer = Integer.parseInt(placeholder.replace("island_top_name_", ""));
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > integer - 1 ? phCheckIfStripped(Utils.getTopIslands().get(integer - 1).getName()) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (placeholder.startsWith("island_top_owner_")) {
            try {
                int integer = Integer.parseInt(placeholder.replace("island_top_owner_", ""));
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > integer - 1 ? User.getUser(Utils.getTopIslands().get(integer - 1).owner).name : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (placeholder.startsWith("island_top_value_")) {
            try {
                int integer = Integer.parseInt(placeholder.replace("island_top_value_", ""));
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > integer - 1 ? Utils.NumberFormatter.format(Utils.getTopIslands().get(integer - 1).value) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            } catch (NumberFormatException ignored) {

            }
        }
        if (placeholder.startsWith("island_top_level_")) {
            try {
                int integer = Integer.parseInt(placeholder.replace("island_top_level_", ""));
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > integer - 1 ? Utils.getTopIslands().get(integer - 1).getFormattedLevel() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            } catch (NumberFormatException ignored) {

            }
        }
        return null;
    }

    public String phCheckIfStripped(String ph) {
        if (IridiumSkyblock.getConfiguration().stripTopIslandPlaceholderColors) {
            return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ph)).replace("\"", "\\\"");
        }
        return ph.replace("\"", "\\\"");
    }

}
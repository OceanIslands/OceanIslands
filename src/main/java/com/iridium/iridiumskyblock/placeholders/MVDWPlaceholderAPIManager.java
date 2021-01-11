package com.iridium.iridiumskyblock.placeholders;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Boosters;
import com.iridium.iridiumskyblock.configs.Upgrades;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MVDWPlaceholderAPIManager {

    public MVDWPlaceholderAPIManager() {
    }

    public void register() {
        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_value", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getFormattedValue() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });
        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_role", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getRole().toString() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_level", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getFormattedLevel() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_rank", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? NumberFormat.getInstance().format(Utils.getIslandRank(user.getIsland())) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_owner", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? User.getUser(user.getIsland().owner).name : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_name", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getName() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_crystals", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getFormattedCrystals() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_members", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? String.valueOf(user.getIsland().members.size()) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_members_online", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            if (user.getIsland() == null) return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            int online = 0;
            for (String member : user.getIsland().members) {
                if (Bukkit.getPlayer(User.getUser(member).name) != null) {
                    online++;
                }
            }
            return String.valueOf(online);
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_bank_vault", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getFormattedMoney() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_bank_experience", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().getFormattedExp() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_biome", e -> {
            Player player = e.getPlayer();
            if (player == null) {
                return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            }
            User user = User.getUser(player);
            return user.getIsland() != null ? user.getIsland().biome.name() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_midnight_seconds", e -> {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            long time = (c.getTimeInMillis() - System.currentTimeMillis()) / 1000;
            int day = (int) TimeUnit.SECONDS.toDays(time);
            int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400L));
            int second = (int) Math.floor((time - day * 86400 - hours * 3600) % 60.00);
            return String.valueOf(second);
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_midnight_minutes", e -> {
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
            return String.valueOf(minute);
        });

        PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_midnight_hours", e -> {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            long time = (c.getTimeInMillis() - System.currentTimeMillis()) / 1000;
            int day = (int) TimeUnit.SECONDS.toDays(time);
            int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400L));
            return String.valueOf(hours);
        });

        for (Upgrades.Upgrade upgrade : IridiumSkyblock.getInstance().getIslandUpgrades()) {
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "island_upgrade_" + upgrade.name + "_level", e->{
                Player player = e.getPlayer();
                if (player == null) {
                    return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
                }
                User user = User.getUser(player);
                int level = user.getIsland() != null ? user.getIsland().getUpgradeLevel(upgrade.name) : 1;
                return user.getIsland() != null ? NumberFormat.getInstance().format(level) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "island_upgrade_" + upgrade.name + "_dimensions", e->{
                Player player = e.getPlayer();
                if (player == null) {
                    return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
                }
                User user = User.getUser(player);
                int level = user.getIsland() != null ? user.getIsland().getUpgradeLevel(upgrade.name) : 1;
                return user.getIsland() != null ? Integer.toString(upgrade.getIslandUpgrade(level).size) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "island_upgrade_" + upgrade.name + "_amount", e->{
                Player player = e.getPlayer();
                if (player == null) {
                    return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
                }
                User user = User.getUser(player);
                int level = user.getIsland() != null ? user.getIsland().getUpgradeLevel(upgrade.name) : 1;
                return user.getIsland() != null ? Integer.toString(upgrade.getIslandUpgrade(level).size) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "island_upgrade_" + upgrade.name + "_count", e->{
                Player player = e.getPlayer();
                if (player == null) {
                    return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
                }
                User user = User.getUser(player);
                int level = user.getIsland() != null ? user.getIsland().getUpgradeLevel(upgrade.name) : 1;
                return user.getIsland() != null ? Integer.toString(upgrade.getIslandUpgrade(level).size) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            });
        }

        for (Boosters.Booster booster : IridiumSkyblock.getInstance().getIslandBoosters()) {
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_booster_"+booster.name, e->{
                Player player = e.getPlayer();
                if (player == null) {
                    return IridiumSkyblock.getConfiguration().placeholderDefaultValue;
                }
                User user = User.getUser(player);
                return user.getIsland() != null ? Integer.toString(user.getIsland().getBoosterTime(booster.name)) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            });
        }

        for (int i = 0; i < 10; i++) { //TODO there is probably a more efficient way to do this?
            int finalI = i;
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_top_owner_" + (i + 1), e -> {
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > finalI ? User.getUser(Utils.getTopIslands().get(finalI).owner).name : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_top_name_" + (i + 1), e -> {
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > finalI ? phCheckIfStripped(Utils.getTopIslands().get(finalI).getName()) : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_top_value_" + (i + 1), e -> {
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > finalI ? Utils.getTopIslands().get(finalI).getFormattedValue() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            });
            PlaceholderAPI.registerPlaceholder(IridiumSkyblock.getInstance(), "iridiumskyblock_island_top_level_" + (i + 1), e -> {
                List<Island> islands = Utils.getTopIslands();
                return islands.size() > finalI ? Utils.getTopIslands().get(finalI).getFormattedLevel() : IridiumSkyblock.getConfiguration().placeholderDefaultValue;
            });
        }
    }

    public String phCheckIfStripped(String ph) {
        if (IridiumSkyblock.getConfiguration().stripTopIslandPlaceholderColors) {
            return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ph)).replace("\"", "\\\"");
        }
        return ph.replace("\"", "\\\"");
    }
}
package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Upgrades;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GiveUpgradeCommand extends Command {

    public GiveUpgradeCommand() {
        super(Collections.singletonList("giveupgrade"), "Give an Island an Upgrade", "iridiumskyblock.giveupgrade", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 4 && args.length != 3) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is giveupgrade <player> <upgrade> <level>");
            return;
        }
        OfflinePlayer player = Bukkit.getPlayer(args[1]);
        if (player != null) {
            Island island = User.getUser(player).getIsland();
            if (island != null) {
                try {
                    for (Upgrades.Upgrade upgrade : IridiumSkyblock.getInstance().getIslandUpgrades()) {
                        if (!args[2].equalsIgnoreCase(upgrade.name)) continue;
                        int level = args.length == 3 ? island.getUpgradeLevel(upgrade.name) + 1 : Integer.parseInt(args[3]);
                        island.setUpgradeLevel(upgrade, level);
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(args[2] + " is not a number");
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerNoIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerOffline.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        execute(sender, args);
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        if (args.length == 3) {
            return IridiumSkyblock.getInstance().getIslandUpgrades().stream().map(upgrade -> upgrade.name).collect(Collectors.toList());
        }
        return null;
    }
}
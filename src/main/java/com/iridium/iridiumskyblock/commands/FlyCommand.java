package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FlyCommand extends Command {

    public FlyCommand() {
        super(Arrays.asList("fly", "flight"), "Toggle your ability to fly", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            if (user.getIsland().isInIsland(p.getLocation())) {
                if (user.getIsland().getBoosterTime(IridiumSkyblock.getBoosters().islandFlightBooster.name) != 0 || p.hasPermission("iridiumskyblock.Fly")) {
                    if (args.length == 2) {
                        switch (args[1].toLowerCase()) {
                            case "on":
                                p.setAllowFlight(true);
                                p.setFlying(true);
                                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().flightEnabled.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                                return;
                            case "off":
                                p.setAllowFlight(false);
                                p.setFlying(false);
                                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().flightDisabled.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                                return;
                        }
                    }
                    if (p.getAllowFlight()) {
                        p.setAllowFlight(false);
                        p.setFlying(false);
                        p.sendMessage(Utils.color(IridiumSkyblock.getMessages().flightDisabled.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    } else {
                        p.setAllowFlight(true);
                        p.setFlying(true);
                        p.sendMessage(Utils.color(IridiumSkyblock.getMessages().flightEnabled.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                    user.flying = p.isFlying();
                } else {
                    p.sendMessage(Utils.color(IridiumSkyblock.getMessages().flightBoosterNotActive.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().mustBeInIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        execute(sender, args);
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return Arrays.asList("on", "off");
    }
}

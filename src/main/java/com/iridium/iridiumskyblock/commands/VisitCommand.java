package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.gui.VisitGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class VisitCommand extends Command {

    public VisitCommand() {
        super(Collections.singletonList("visit"), "Visit another players island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (args.length != 2) {
            VisitGUI visitGUI = IridiumSkyblock.getInstance().getVisitGUI().getPage(1);
            visitGUI.addContent();
            p.openInventory(visitGUI.getInventory());
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        User user = User.getUser(player);
        if (user.getIsland() != null) {
            if (user.getIsland().visit || User.getUser(p).bypassing || p.hasPermission("iridiumskyblock.visitbypass")) {
                user.getIsland().teleportHome(p);
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        execute(sender, args);
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}

package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class PurgeCommand extends Command {

    public PurgeCommand() {
        super(Arrays.asList("purge"), "Purge old islands", "iridiumskyblock.purge", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (IridiumSkyblock.getIslandManager().id != 0) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().calculationAlreadyInProcess.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            return;
        }
        IridiumSkyblock.getIslandManager().purgeIslands(90, sender);
        int total = IridiumSkyblock.getIslandManager().islands.size();
        double totalSeconds = total * 30;
        int minutes = (int) Math.floor(totalSeconds / 60.00);
        double seconds = (int) (totalSeconds - (minutes * 60));
        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().purgingIslands.replace("%seconds%", String.valueOf(seconds)).replace("%minutes%", String.valueOf(minutes)).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
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

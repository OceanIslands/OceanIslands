package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.gui.ConfirmationGUI;
import com.iridium.iridiumskyblock.managers.ClaimManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.managers.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ResetAllDataCommand extends Command {

    public ResetAllDataCommand() {
        super(Collections.singletonList("resetalldata"), "Resets all plugin data", "iridiumskyblock.resetalldata", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Runnable runnable = () -> {
            IridiumSkyblock.getSqlManager().deleteAll();
            UserManager.cache.clear();
            IslandManager.cache.clear();
            ClaimManager.cache.clear();
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().dataReset.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        };

        if (sender instanceof Player) {
            Player player = (Player) sender;
            ConfirmationGUI confirmationGUI = new ConfirmationGUI(runnable, "Reset all data");
            player.openInventory(confirmationGUI.getInventory());
        } else {
            runnable.run();
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

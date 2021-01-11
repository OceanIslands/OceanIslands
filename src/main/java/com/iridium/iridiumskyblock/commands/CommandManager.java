package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Schematics;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandManager implements CommandExecutor, TabCompleter {

    public List<Command> commands = new ArrayList<>();

    public CommandManager(String command) {
        IridiumSkyblock.getInstance().getCommand(command).setExecutor(this);
        IridiumSkyblock.getInstance().getCommand(command).setTabCompleter(this);
    }

    public void registerCommands() {
        List<String> manuallyRegisteredCommands = Arrays.asList("shopCommand");
        Arrays.stream(IridiumSkyblock.getCommands().getClass().getFields())
                .filter(field -> !manuallyRegisteredCommands.contains(field.getName()))
                .map(field -> {
                    try {
                        return (Command) field.get(IridiumSkyblock.getCommands());
                    } catch (IllegalAccessException exception) {
                        exception.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(command -> command.getClass().getSuperclass() == Command.class)
                .forEach(this::registerCommand);

        if (IridiumSkyblock.getConfiguration().islandShop) {
            registerCommand(IridiumSkyblock.getCommands().shopCommand);
        }
    }

    public void registerCommand(Command command) {
        commands.add(command);
    }

    public void unRegisterCommand(Command command) {
        commands.remove(command);
    }

    @Override
    public boolean onCommand(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        if (!IridiumSkyblock.getConfiguration().mainCommandPerm.equalsIgnoreCase("") && !cs
                .hasPermission(IridiumSkyblock.getConfiguration().mainCommandPerm)) {
            cs.sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermission
                    .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            return false;
        }
        if (args.length != 0) {
            for (Command command : commands) {
                if (command.aliases.contains(args[0]) && command.enabled) {
                    if (command.player && !(cs instanceof Player)) {
                        // Must be a player
                        cs.sendMessage(Utils.color(IridiumSkyblock.getMessages().mustBeAPlayer
                                .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        return true;
                    }
                    if ((cs.hasPermission(command.permission) || command.permission
                            .equalsIgnoreCase("") || command.permission
                            .equalsIgnoreCase("iridiumskyblock.")) && command.enabled) {
                        command.execute(cs, args);
                    } else {
                        // No permission
                        cs.sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermission
                                .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                    return true;
                }
            }
        } else {
            if (cs instanceof Player) {
                Player p = (Player) cs;
                User u = User.getUser(p);
                if (u.getIsland() != null) {
                    if (u.getIsland().schematic == null) {
                        if (IridiumSkyblock.getSchematics().schematicList.size() == 1) {
                            for (Schematics.FakeSchematic schematic : IridiumSkyblock.getSchematics().schematicList) {
                                u.getIsland().schematic = schematic.overworldData.schematic;
                                u.getIsland().netherschematic = schematic.netherData.schematic;
                                break;
                            }
                        } else {
                            p.openInventory(u.getIsland().schematicSelectGUI.getInventory());
                            return true;
                        }
                    }
                    if (IridiumSkyblock.getConfiguration().islandMenu) {
                        p.openInventory(u.getIsland().islandMenuGUI.getInventory());
                    } else {
                        u.getIsland().teleportHome(p);
                    }
                } else {
                    IslandManager.createIsland(p);
                }
                return true;
            }
        }
        cs.sendMessage(Utils.color(IridiumSkyblock.getMessages().unknownCommand
                .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();
            for (Command command : commands) {
                for (String alias : command.aliases) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && (
                            command.enabled && (cs.hasPermission(command.permission)
                                    || command.permission.equalsIgnoreCase("") || command.permission
                                    .equalsIgnoreCase("iridiumskyblock.")))) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }
        for (Command command : commands) {
            if (command.aliases.contains(args[0]) && (command.enabled && (
                    cs.hasPermission(command.permission) || command.permission.equalsIgnoreCase("")
                            || command.permission.equalsIgnoreCase("iridiumskyblock.")))) {
                return command.TabComplete(cs, cmd, s, args);
            }
        }
        return null;
    }

}

package com.iridium.iridiumskyblock.commands;

import com.cryptomorin.xseries.XBiome;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Config;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BiomeCommand extends Command {

    public BiomeCommand() {
        super(Collections.singletonList("biome"), "Opens the biome GUI", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        World.Environment environment = p.getWorld().getEnvironment();
        Island island = user.getIsland();
        if (island != null) {
            if (args.length == 2) {
                Optional<XBiome> optionalXBiome = XBiome.matchXBiome(args[1]);
                if (optionalXBiome.isPresent() && IridiumSkyblock.getConfiguration().islandBiomes.containsKey(optionalXBiome.get())) {
                    XBiome xBiome = optionalXBiome.get();
                    Config.BiomeConfig biomeConfig = IridiumSkyblock.getConfiguration().islandBiomes.get(xBiome);
                    Utils.BuyResponse response = Utils.canBuy(p, biomeConfig.price, biomeConfig.crystals);
                    if (response == Utils.BuyResponse.SUCCESS) {
                        switch (xBiome.getEnvironment()) {
                            case NORMAL:
                                island.setBiome(xBiome);
                                break;
                            case NETHER:
                                island.setNetherBiome(xBiome);
                        }
                        p.sendMessage(Utils.color(IridiumSkyblock.getMessages().biomePurchased
                                .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                                .replace("%biome%", WordUtils.capitalize(xBiome.name().toLowerCase().replace("_", " ")))
                                .replace("%crystals%", biomeConfig.crystals + "")
                                .replace("%money", Utils.NumberFormatter.format(biomeConfig.price))));

                        for (String member : island.members) {
                            Player pl = Bukkit.getPlayer(User.getUser(member).name);
                            if (pl != null) {
                                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().biomeChanged
                                        .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                                        .replace("%biome%", xBiome.name()).replace("%player%", p.getName())));
                            }
                        }
                    } else {
                        p.sendMessage(Utils.color((response == Utils.BuyResponse.NOT_ENOUGH_VAULT ? IridiumSkyblock.getMessages().cantBuy : IridiumSkyblock.getMessages().notEnoughCrystals).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().unknownBiome.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else {
                switch (environment) {
                    case NORMAL:
                        p.openInventory(island.biomeGUI.getPage(1).getInventory());
                        break;
                    case NETHER:
                        p.openInventory(island.netherBiomeGUI.getPage(1).getInventory());
                }
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (island != null) {
            switch (p.getWorld().getEnvironment()) {
                case NORMAL:
                    p.openInventory(island.biomeGUI.getPage(1).getInventory());
                    break;
                case NETHER:
                    p.openInventory(island.netherBiomeGUI.getPage(1).getInventory());
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return Arrays.stream(XBiome.values()).map(Enum::name).filter(s1 -> s1.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
    }
}

package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.configs.Shop;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShopCommand extends Command {

    public ShopCommand() {
        super(Collections.singletonList("shop"), "Access the Skyblock Shop", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (args.length > 1) {
            String shopName = args[1];
            for (Shop.ShopObject shopObject : IridiumSkyblock.getShop().shop) {
                if (shopObject.name.equalsIgnoreCase(shopName)) {
                    p.openInventory(IridiumSkyblock.getShopGUI().pages.getPage(shopObject.slot).getPage(1).getInventory());
                    return;
                }
            }
        }
        p.openInventory(IridiumSkyblock.getShopGUI().getInventory());
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        execute(sender, args);
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return IridiumSkyblock.getShop().shop.stream().map(shopObject -> shopObject.name).collect(Collectors.toList());
    }
}

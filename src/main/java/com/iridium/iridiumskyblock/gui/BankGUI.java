package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.bank.BankItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;

public class BankGUI extends GUI implements Listener {
    public BankGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().bankGUISize, IridiumSkyblock.getInventories().bankGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland() != null) {
            for (BankItem bankItem : IridiumSkyblock.getInstance().getBankItems()) {
                if (!bankItem.isEnabled()) continue;
                setItem(bankItem.getItem().slot, Utils.makeItem(bankItem.getItem(), Collections.singletonList(new Utils.Placeholder("amount", bankItem.getValue(getIsland()).toString()))));
            }

            if (IridiumSkyblock.getInventories().backButtons) {
                setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
            }
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            Player p = (Player) e.getWhoClicked();
            Island island = getIsland();
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().islandMenuGUI.getInventory());
                return;
            }
            if (!IridiumSkyblock.getConfiguration().bankWithdrawing) {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().withdrawDisabled
                        .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                return;
            }
            for (BankItem bankItem : IridiumSkyblock.getInstance().getBankItems()) {
                if (bankItem.getItem().slot != e.getSlot() || !bankItem.isEnabled()) continue;
                switch (e.getClick()) {
                    case SHIFT_LEFT:
                        bankItem.withdraw(p, island, Integer.MAX_VALUE);
                        break;
                    case LEFT:
                        bankItem.withdraw(p, island, bankItem.getDefaultWithdraw());
                        break;
                    case SHIFT_RIGHT:
                        bankItem.deposit(p, island, Integer.MAX_VALUE);
                        break;
                    case RIGHT:
                        bankItem.deposit(p, island, bankItem.getDefaultWithdraw());
                        break;
                }
            }
        }
    }
}

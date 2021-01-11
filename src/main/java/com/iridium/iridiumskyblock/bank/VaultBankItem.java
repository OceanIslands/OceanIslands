package com.iridium.iridiumskyblock.bank;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.configs.Inventories;
import org.bukkit.entity.Player;

public class VaultBankItem implements BankItem<Double> {

    private final Inventories.Item item;
    private final boolean enabled = true;

    public VaultBankItem(Inventories.Item item) {
        this.item = item;
    }

    @Override
    public Inventories.Item getItem() {
        return item;
    }

    @Override
    public Double getValue(Island island) {
        return island.getMoney();
    }

    @Override
    public void withdraw(Player player, Island island, Double amount) {
        double current = getValue(island);
        if (amount > current) amount = current;
        if (amount == 0) return;
        island.setMoney(current - amount);
        IridiumSkyblock.getInstance().getEconomy().depositPlayer(player, amount);
    }

    @Override
    public void deposit(Player player, Island island, Double amount) {
        double current = IridiumSkyblock.getInstance().getEconomy().getBalance(player);
        if (amount > current) amount = current;
        if (amount == 0) return;
        island.setMoney(getValue(island) + amount);
        IridiumSkyblock.getInstance().getEconomy().withdrawPlayer(player, amount);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Double getDefaultWithdraw() {
        return 1000.00;
    }
}

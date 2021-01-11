package com.iridium.iridiumskyblock.bank;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Inventories;
import org.bukkit.entity.Player;

public class ExperienceBankItem implements BankItem<Integer> {

    private final Inventories.Item item;
    private final boolean enabled = true;

    public ExperienceBankItem(Inventories.Item item) {
        this.item = item;
    }

    @Override
    public Inventories.Item getItem() {
        return item;
    }

    @Override
    public Integer getValue(Island island) {
        return island.getExperience();
    }

    @Override
    public void withdraw(Player player, Island island, Integer amount) {
        int current = getValue(island);
        if (amount > current) amount = current;
        if (amount == 0) return;
        island.setExperience(getValue(island) - amount);
        Utils.setTotalExperience(player, Utils.getTotalExperience(player) + amount);
    }

    @Override
    public void deposit(Player player, Island island, Integer amount) {
        int current = Utils.getTotalExperience(player);
        if (amount > current) amount = current;
        if (amount == 0) return;
        island.setExperience(getValue(island) + amount);
        Utils.setTotalExperience(player, Utils.getTotalExperience(player) - amount);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Integer getDefaultWithdraw() {
        return 100;
    }
}

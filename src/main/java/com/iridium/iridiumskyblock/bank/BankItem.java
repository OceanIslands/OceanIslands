package com.iridium.iridiumskyblock.bank;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.configs.Inventories;
import org.bukkit.entity.Player;

public interface BankItem<T> {
    void withdraw(Player player, Island island, T amount);

    void deposit(Player player, Island island, T amount);

    T getValue(Island island);

    Inventories.Item getItem();

    boolean isEnabled();

    T getDefaultWithdraw();
}

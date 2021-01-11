package com.iridium.iridiumskyblock.bank;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Inventories;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CrystalsBankItem implements BankItem<Integer> {

    private final Inventories.Item item;
    private final boolean enabled = true;

    public CrystalsBankItem(Inventories.Item item) {
        this.item = item;
    }

    @Override
    public Inventories.Item getItem() {
        return item;
    }

    @Override
    public Integer getValue(Island island) {
        return island.getCrystals();
    }

    @Override
    public void withdraw(Player player, Island island, Integer amount) {
        int current = getValue(island);
        if (amount > current) amount = current;
        if (amount == 0) return;
        island.setCrystals(current - amount);
        addCrystals(player, amount);
    }

    @Override
    public void deposit(Player player, Island island, Integer amount) {
        int i = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) continue;
            int crystals = Utils.getCrystals(itemStack) * itemStack.getAmount();
            if (crystals != 0) {
                double current = getValue(island);
                island.setCrystals((int) (current + crystals));
                player.getInventory().clear(i);
                if (amount != 0) return;
            }
            i++;
        }
    }

    private void addCrystals(Player player, double amount) {
        ItemStack itemStack = Utils.getCrystals((int) amount);
        if (Utils.hasOpenSlot(player.getInventory())) {
            player.getInventory().addItem(itemStack);
        } else {
            player.getLocation().getWorld().dropItem(player.getLocation(), itemStack);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Integer getDefaultWithdraw() {
        return 10;
    }
}

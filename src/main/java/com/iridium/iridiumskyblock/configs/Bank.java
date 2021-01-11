package com.iridium.iridiumskyblock.configs;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.bank.CrystalsBankItem;
import com.iridium.iridiumskyblock.bank.ExperienceBankItem;
import com.iridium.iridiumskyblock.bank.VaultBankItem;

import java.util.Arrays;

public class Bank {
    public VaultBankItem vaultBankItem = new VaultBankItem(new Inventories.Item(XMaterial.PAPER, 15, 1, "&b&lIsland Money", Arrays.asList("&7${amount}", "&b&l[!] &bLeft click to withdraw", "&b&l[!] &bRight click to deposit")));
    public CrystalsBankItem crystalsBankItem = new CrystalsBankItem(new Inventories.Item(XMaterial.NETHER_STAR, 13, 1, "&b&lIsland Crystals", Arrays.asList("&7{amount} Crystals", "&b&l[!] &bLeft click to withdraw", "&b&l[!] &bRight click to deposit")));
    public ExperienceBankItem experienceBankItem = new ExperienceBankItem(new Inventories.Item(XMaterial.EXPERIENCE_BOTTLE, 11, 1, "&b&lIsland Experience", Arrays.asList("&7{amount} Experience", "&b&l[!] &bLeft click to withdraw", "&b&l[!] &bRight click to deposit")));
}

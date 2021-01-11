package com.iridium.iridiumskyblock;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumcolorapi.IridiumColorAPI;
import com.iridium.iridiumskyblock.configs.Boosters;
import com.iridium.iridiumskyblock.configs.Inventories;
import com.iridium.iridiumskyblock.configs.Upgrades;
import com.iridium.iridiumskyblock.managers.IslandManager;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    private static final boolean supports = XMaterial.supports(14);

    public static XMaterialItemId xMaterialItemId;

    static {
        {
            InputStream inputStream = IridiumSkyblock.getInstance().getResource("itemdata.json");
            Scanner sc = new Scanner(inputStream);
            //Reading line by line from scanner to StringBuffer
            StringBuffer content = new StringBuffer();
            while (sc.hasNext()) {
                content.append(sc.nextLine());
            }
            xMaterialItemId = IridiumSkyblock.getPersist().load(XMaterialItemId.class, content.toString());
        }
    }

    public static ItemStack makeItem(XMaterial material, int amount, String name) {
        ItemStack item = material.parseItem();
        if (item == null) return null;
        item.setAmount(amount);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack makeItem(XMaterial material, int amount, String name, List<String> lore) {
        ItemStack item = material.parseItem();
        if (item == null) return null;
        item.setAmount(amount);
        ItemMeta m = item.getItemMeta();
        m.setLore(Utils.color(lore));
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack makeItem(Inventories.Item item, List<Placeholder> placeholders) {
        try {
            ItemStack itemstack = makeItem(item.material, item.amount, processMultiplePlaceholders(item.title, placeholders), processMultiplePlaceholders(item.lore, placeholders));
            if (item.material == XMaterial.PLAYER_HEAD && item.headData != null) {
                NBTItem nbtItem = new NBTItem(itemstack);
                NBTCompound skull = nbtItem.addCompound("SkullOwner");
                if (supports) {
                    skull.setUUID("Id", UUID.randomUUID());
                } else {
                    skull.setString("Id", UUID.randomUUID().toString());
                }
                NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
                texture.setString("Value", item.headData);
                return nbtItem.getItem();
            } else if (item.material == XMaterial.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(processMultiplePlaceholders(item.headOwner, placeholders));
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItem(XMaterial.STONE, item.amount, processMultiplePlaceholders(item.title, placeholders), processMultiplePlaceholders(item.lore, placeholders));
        }
    }

    public static ItemStack makeItem(Inventories.Item item) {
        try {
            ItemStack itemstack = makeItem(item.material, item.amount, item.title, item.lore);
            if (item.material == XMaterial.PLAYER_HEAD && item.headData != null) {
                NBTItem nbtItem = new NBTItem(itemstack);
                NBTCompound skull = nbtItem.addCompound("SkullOwner");
                if (supports) {
                    skull.setUUID("Id", UUID.randomUUID());
                } else {
                    skull.setString("Id", UUID.randomUUID().toString());
                }
                NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
                texture.setString("Value", item.headData);
                return nbtItem.getItem();
            } else if (item.material == XMaterial.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItem(XMaterial.STONE, item.amount, item.title, item.lore);
        }
    }

    public static ItemStack makeItem(Inventories.Item item, Island island) {
        try {
            ItemStack itemstack = makeItem(item.material, item.amount, processIslandPlaceholders(item.title, island), color(processIslandPlaceholders(item.lore, island)));
            if (item.material == XMaterial.PLAYER_HEAD && item.headData != null) {
                NBTItem nbtItem = new NBTItem(itemstack);
                NBTCompound skull = nbtItem.addCompound("SkullOwner");
                if (supports) {
                    skull.setUUID("Id", UUID.randomUUID());
                } else {
                    skull.setString("Id", UUID.randomUUID().toString());
                }
                NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
                texture.setString("Value", item.headData);
                return nbtItem.getItem();
            } else if (item.material == XMaterial.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItem(XMaterial.STONE, item.amount, processIslandPlaceholders(item.title, island), color(processIslandPlaceholders(item.lore, island)));
        }
    }

    public static ItemStack makeItemHidden(Inventories.Item item) {
        try {
            ItemStack itemstack = makeItemHidden(item.material, item.amount, item.title, item.lore);
            if (item.material == XMaterial.PLAYER_HEAD && item.headData != null) {
                NBTItem nbtItem = new NBTItem(itemstack);
                NBTCompound skull = nbtItem.addCompound("SkullOwner");
                if (supports) {
                    skull.setUUID("Id", UUID.randomUUID());
                } else {
                    skull.setString("Id", UUID.randomUUID().toString());
                }
                NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
                texture.setString("Value", item.headData);
                return nbtItem.getItem();
            } else if (item.material == XMaterial.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItemHidden(XMaterial.STONE, item.amount, item.title, item.lore);
        }
    }

    public static ItemStack makeItemHidden(Inventories.Item item, Island island) {
        return makeItemHidden(item, getIslandPlaceholders(island));
    }

    public static ItemStack makeItemHidden(Inventories.Item item, List<Placeholder> placeholders) {
        try {
            ItemStack itemstack = makeItemHidden(item.material, item.amount, processMultiplePlaceholders(item.title, placeholders), color(processMultiplePlaceholders(item.lore, placeholders)));
            if (item.material == XMaterial.PLAYER_HEAD && item.headData != null) {
                NBTItem nbtItem = new NBTItem(itemstack);
                NBTCompound skull = nbtItem.addCompound("SkullOwner");
                if (supports) {
                    skull.setUUID("Id", UUID.randomUUID());
                } else {
                    skull.setString("Id", UUID.randomUUID().toString());
                }
                NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
                texture.setString("Value", item.headData);
                return nbtItem.getItem();
            } else if (item.material == XMaterial.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            e.printStackTrace();
            return makeItemHidden(XMaterial.STONE, item.amount, processMultiplePlaceholders(item.title, placeholders), color(processMultiplePlaceholders(item.lore, placeholders)));
        }
    }

    public static ItemStack makeItemHidden(XMaterial material, int amount, String name, List<String> lore) {
        ItemStack item = material.parseItem();
        if (item == null) return null;
        item.setAmount(amount);
        ItemMeta m = item.getItemMeta();
        m.setLore(Utils.color(lore));
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static String color(String string) {
        return IridiumColorAPI.process(string);
    }

    public static List<String> color(List<String> strings) {
        return strings.stream().map(Utils::color).collect(Collectors.toList());
    }

    public static boolean isBlockValuable(Block b) {
        for (Upgrades.IslandUpgrade islandUpgrade : IridiumSkyblock.getUpgrades().islandBlockLimitUpgrade.upgrades.values()) {
            if (((Upgrades.IslandBlockLimitUpgrade) islandUpgrade).limitedBlocks.containsKey(XMaterial.matchXMaterial(b.getType())))
                return true;
        }
        return IridiumSkyblock.getBlockValues().blockvalue.containsKey(XMaterial.matchXMaterial(b.getType())) || b.getState() instanceof CreatureSpawner;
    }

    public static boolean isBlockValuable(XMaterial material) {
        for (Upgrades.IslandUpgrade islandUpgrade : IridiumSkyblock.getUpgrades().islandBlockLimitUpgrade.upgrades.values()) {
            if (((Upgrades.IslandBlockLimitUpgrade) islandUpgrade).limitedBlocks.containsKey(material)) return true;
        }
        return IridiumSkyblock.getBlockValues().blockvalue.containsKey(material);
    }

    public static List<Island> getTopIslands() {
        List<Island> islands = IslandManager.getLoadedIslands();
        islands.sort(Comparator.comparingDouble(is -> is.value));
        Collections.reverse(islands);
        return islands;
    }

    public static List<Island> getIslands() {
        List<Island> islands = IslandManager.getLoadedIslands();
        islands.sort(Comparator.comparingInt(Island::getVotes));
        Collections.reverse(islands);
        return islands;
    }

    public static boolean isSafe(Location loc, Island island) {
        if (loc == null) return false;
        if (loc.getY() < 1) return false;
        if (!island.isInIsland(loc)) return false;
        if (!loc.getBlock().getType().name().endsWith("AIR")) return false;
        if (loc.clone().add(0, -1, 0).getBlock().getType().name().endsWith("AIR"))
            return false;
        return !loc.clone().add(0, -1, 0).getBlock().isLiquid();
    }

    public static int getIslandRank(Island island) {
        int i = 1;
        for (Island is : getTopIslands()) {
            if (is.equals(island)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public static Location getNewHome(Island island, Location loc) {
        Block b;
        if (loc != null) {
            b = loc.getWorld().getHighestBlockAt(loc);
            while (!XMaterial.matchXMaterial(b.getType()).name().endsWith("AIR")) {
                b = b.getLocation().clone().add(0, 1, 0).getBlock();
            }
            if (isSafe(b.getLocation(), island)) {
                return b.getLocation().add(0.5, 0, 0.5);
            }
        }

        for (double X = island.pos1.getX(); X <= island.pos2.getX(); X++) {
            for (double Z = island.pos1.getZ(); Z <= island.pos2.getZ(); Z++) {
                b = loc.getWorld().getHighestBlockAt((int) X, (int) Z);
                while (!XMaterial.matchXMaterial(b.getType()).name().endsWith("AIR")) {
                    b = b.getLocation().clone().add(0, 1, 0).getBlock();
                }
                if (isSafe(b.getLocation(), island)) {
                    return b.getLocation().add(0.5, 0, 0.5);
                }
            }
        }
        return null;
    }

    public static List<Placeholder> getIslandPlaceholders(Island island) {
        List<Placeholder> placeholders = new ArrayList<>(Arrays.asList(
                new Placeholder("experience", island.getFormattedExp()),
                new Placeholder("crystals", island.getFormattedCrystals()),
                new Placeholder("money", island.getFormattedMoney()),
                new Placeholder("value", island.getFormattedValue())
        ));
        for (Upgrades.Upgrade upgrade : IridiumSkyblock.getInstance().getIslandUpgrades()) {
            int level = island.getUpgradeLevel(upgrade.name);
            placeholders.add(new Placeholder(upgrade.name + "vaultcost", upgrade.getIslandUpgrade(level + 1) != null ? Integer.toString(upgrade.getIslandUpgrade(level + 1).vaultCost) : IridiumSkyblock.getMessages().maxlevelreached));
            placeholders.add(new Placeholder(upgrade.name + "crystalscost", upgrade.getIslandUpgrade(level + 1) != null ? Integer.toString(upgrade.getIslandUpgrade(level + 1).crystalsCost) : IridiumSkyblock.getMessages().maxlevelreached));
            placeholders.add(new Placeholder(upgrade.name + "level", Integer.toString(level)));
            if (upgrade.getIslandUpgrade(level).size != null) {
                placeholders.add(new Placeholder(upgrade.name + "blocks", Integer.toString(upgrade.getIslandUpgrade(level).size)));
                placeholders.add(new Placeholder(upgrade.name + "count", Integer.toString(upgrade.getIslandUpgrade(level).size)));
            }
        }
        for (Boosters.Booster booster : IridiumSkyblock.getInstance().getIslandBoosters()) {
            placeholders.add(new Placeholder(booster.name + "booster", Integer.toString(island.getBoosterTime(booster.name))));
            placeholders.add(new Placeholder(booster.name + "booster_seconds", Integer.toString(island.getBoosterTime(booster.name) % 60)));
            placeholders.add(new Placeholder(booster.name + "booster_minutes", Integer.toString((int) Math.floor(island.getBoosterTime(booster.name) / 60.00))));
            placeholders.add(new Placeholder(booster.name + "booster_crystalcost", Integer.toString(booster.crystalsCost)));
            placeholders.add(new Placeholder(booster.name + "booster_vaultcost", Integer.toString(booster.vaultCost)));
        }
        return placeholders;
    }

    public static String processIslandPlaceholders(String line, Island island) {
        return processMultiplePlaceholders(line, getIslandPlaceholders(island));
    }

    public static List<String> processIslandPlaceholders(List<String> lines, Island island) {
        return lines.stream().map(s -> processIslandPlaceholders(s, island)).collect(Collectors.toList());
    }

    public static List<String> processMultiplePlaceholders(List<String> lines, List<Placeholder> placeholders) {
        return lines.stream().map(s -> processMultiplePlaceholders(s, placeholders)).collect(Collectors.toList());
    }

    public static String processMultiplePlaceholders(String line, List<Placeholder> placeholders) {
        for (Placeholder placeholder : placeholders) {
            line = placeholder.process(line);
        }
        return color(line);
    }

    public static void pay(Player p, double vault, int crystals) {
        User u = User.getUser(p);
        Island island = u.getIsland();
        if (island != null) {
            island.setCrystals(island.getCrystals() + crystals);
            if (IridiumSkyblock.getInstance().getEconomy() == null) {
                island.setMoney(island.getMoney() + vault);
            } else {
                IridiumSkyblock.getInstance().getEconomy().depositPlayer(p, vault);
            }
        } else {
            if (IridiumSkyblock.getInstance().getEconomy() == null) {
                IridiumSkyblock.getInstance().getLogger().warning("Vault plugin not found");
                return;
            }
            IridiumSkyblock.getInstance().getEconomy().depositPlayer(p, vault);
        }
    }

    public static BuyResponse canBuy(Player p, double vault, int crystals) {
        User u = User.getUser(p);
        Island island = u.getIsland();
        if (island != null) {
            if (island.getCrystals() < crystals) return BuyResponse.NOT_ENOUGH_CRYSTALS;
            if (IridiumSkyblock.getInstance().getEconomy() != null) {
                if (IridiumSkyblock.getInstance().getEconomy().getBalance(p) >= vault) {
                    IridiumSkyblock.getInstance().getEconomy().withdrawPlayer(p, vault);
                    island.setCrystals(island.getCrystals() - crystals);
                    return BuyResponse.SUCCESS;
                }
            }
            if (island.getMoney() >= vault) {
                island.setMoney(island.getMoney() - vault);
                island.setCrystals(island.getCrystals() - crystals);
                return BuyResponse.SUCCESS;
            }
            return BuyResponse.NOT_ENOUGH_VAULT;
        }
        if (IridiumSkyblock.getInstance().getEconomy() != null) {
            if (IridiumSkyblock.getInstance().getEconomy().getBalance(p) >= vault && crystals == 0) {
                IridiumSkyblock.getInstance().getEconomy().withdrawPlayer(p, vault);
                return BuyResponse.SUCCESS;
            }
        }
        return crystals == 0 ? BuyResponse.NOT_ENOUGH_VAULT : BuyResponse.NOT_ENOUGH_CRYSTALS;
    }

    public static int getExpAtLevel(final int level) {
        if (level <= 15) {
            return (2 * level) + 7;
        } else if (level <= 30) {
            return (5 * level) - 38;
        }
        return (9 * level) - 158;
    }

    public static int getTotalExperience(final Player player) {
        int exp = Math.round(getExpAtLevel(player.getLevel()) * player.getExp());
        int currentLevel = player.getLevel();

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    public static void setTotalExperience(final Player player, final int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("Experience is negative!");
        }
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        int amount = exp;
        while (amount > 0) {
            final int expToLevel = getExpAtLevel(player.getLevel());
            amount -= expToLevel;
            if (amount >= 0) {
                // give until next level
                player.giveExp(expToLevel);
            } else {
                // give the rest
                amount += expToLevel;
                player.giveExp(amount);
                amount = 0;
            }
        }
    }

    public static ItemStack getCrystals(int amount) {
        ItemStack itemStack = makeItemHidden(IridiumSkyblock.getInventories().crystal, Collections.singletonList(new Placeholder("amount", amount + "")));
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("crystals", amount);
        return nbtItem.getItem();
    }

    public static int getCrystals(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return 0;
        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.hasKey("crystals")) {
            return nbtItem.getInteger("crystals");
        }
        return 0;
    }


    public static String getCurrentTimeStamp(Date date, String format) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);//dd/MM/yyyy
        return sdfDate.format(date);
    }

    public static Date getLocalDateTime(String time, String format) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);//dd/MM/yyyy
        try {
            return sdfDate.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    public static int getAmount(Inventory inventory, XMaterial materials) {
        int total = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;
            if (materials.isSimilar(item)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    public static void removeAmount(Inventory inventory, XMaterial material, int amount) {
        int removed = 0;
        int index = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) {
                index++;
                continue;
            }
            if (removed >= amount) break;
            if (itemStack != null) {
                if (material.isSimilar(itemStack)) {
                    if (removed + itemStack.getAmount() <= amount) {
                        removed += itemStack.getAmount();
                        inventory.setItem(index, null);
                    } else {
                        itemStack.setAmount(itemStack.getAmount() - (amount - removed));
                        removed += amount;
                    }
                }
            }
            index++;
        }
    }

    public static boolean hasOpenSlot(Inventory inv) {
        return inv.firstEmpty() == -1;
    }

    public static XMaterial getXMaterialFromId(int id, byte data) {
        for (MaterialItemId materialItemId : xMaterialItemId.items) {
            if (materialItemId.type == id && materialItemId.meta == data) {
                return XMaterial.matchXMaterial(materialItemId.name).get();
            }
        }
        return null;
    }

    public static class XMaterialItemId {
        List<MaterialItemId> items;
    }

    public static class MaterialItemId {
        int type;
        byte meta;
        String name;
    }

    public static class Placeholder {

        private final String key;
        private final String value;

        public Placeholder(String key, String value) {
            this.key = "{" + key + "}";
            this.value = value;
        }

        public String process(String line) {
            if (line == null) return "";
            return line.replace(key, value);
        }
    }

    public enum BuyResponse {
        SUCCESS,
        NOT_ENOUGH_CRYSTALS,
        NOT_ENOUGH_VAULT
    }

    public static class NumberFormatter {
        private static final String FORMAT = "%." + IridiumSkyblock.getConfiguration().numberAbbreviationDecimalPlaces + "f";
        private static final long ONE_THOUSAND_LONG = 1000;
        private static final long ONE_MILLION_LONG = 1000000;
        private static final long ONE_BILLION_LONG = 1000000000;

        private static final BigDecimal ONE_THOUSAND = new BigDecimal(1000);
        private static final BigDecimal ONE_MILLION = new BigDecimal(1000000);
        private static final BigDecimal ONE_BILLION = new BigDecimal(1000000000);

        public static String format(double number) {
            if (!IridiumSkyblock.getConfiguration().displayNumberAbbreviations) {
                return NumberFormat.getInstance().format(number);
            }
            return IridiumSkyblock.getConfiguration().prettierAbbreviations ? formatPrettyNumber(new BigDecimal(number)) : formatNumber(number);
        }

        private static String formatNumber(double number) {
            StringBuilder output = new StringBuilder();

            if (number < 0) {
                output.append("ERROR");
            } else if (number < ONE_THOUSAND_LONG) {
                output.append(String.format(FORMAT, number));
            } else if (number < ONE_MILLION_LONG) {
                output.append(String.format(FORMAT, number / ONE_THOUSAND_LONG)).append(IridiumSkyblock.getConfiguration().thousandAbbreviation);
            } else if (number < ONE_BILLION_LONG) {
                output.append(String.format(FORMAT, number / ONE_MILLION_LONG)).append(IridiumSkyblock.getConfiguration().millionAbbreviation);
            } else {
                output.append(String.format(FORMAT, number / ONE_BILLION_LONG)).append(IridiumSkyblock.getConfiguration().billionAbbreviation);
            }

            return output.toString();
        }

        private static String formatPrettyNumber(BigDecimal bigDecimal) {
            bigDecimal = bigDecimal.setScale(IridiumSkyblock.getConfiguration().numberAbbreviationDecimalPlaces, RoundingMode.HALF_DOWN);
            StringBuilder outputStringBuilder = new StringBuilder();

            if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                outputStringBuilder
                        .append("-")
                        .append(formatPrettyNumber(bigDecimal.negate()));
            } else if (bigDecimal.compareTo(ONE_THOUSAND) < 0) {
                outputStringBuilder
                        .append(bigDecimal.stripTrailingZeros().toPlainString());
            } else if (bigDecimal.compareTo(ONE_MILLION) < 0) {
                outputStringBuilder
                        .append(bigDecimal.divide(ONE_THOUSAND, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString())
                        .append(IridiumSkyblock.getConfiguration().thousandAbbreviation);
            } else if (bigDecimal.compareTo(ONE_BILLION) < 0) {
                outputStringBuilder
                        .append(bigDecimal.divide(ONE_MILLION, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString())
                        .append(IridiumSkyblock.getConfiguration().millionAbbreviation);
            } else {
                outputStringBuilder
                        .append(bigDecimal.divide(ONE_BILLION, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString())
                        .append(IridiumSkyblock.getConfiguration().billionAbbreviation);
            }

            return outputStringBuilder.toString();
        }
    }

}

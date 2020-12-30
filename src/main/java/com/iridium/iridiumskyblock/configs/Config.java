package com.iridium.iridiumskyblock.configs;

import com.cryptomorin.xseries.XBiome;
import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.MissionRestart;
import com.iridium.iridiumskyblock.Permissions;
import com.iridium.iridiumskyblock.Role;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.entity.EntityType;

public class Config {
    public String prefix = "&b&lOceanIslands &8»";
    public String worldName = "OceanIslands";
    public String netherWorldName = worldName + "_nether";
    public String chatRankPlaceholder = "[ISLAND_RANK]";
    public String chatValuePlaceholder = "[ISLAND_VALUE]";
    public String chatNAMEPlaceholder = "[ISLAND_NAME]";
    public String chatLevelPlaceholder = "[ISLAND_LEVEL]";
    public String placeholderDefaultValue = "N/A";
    public String thousandAbbreviation = "K";
    public String millionAbbreviation = "M";
    public String billionAbbreviation = "B";
    public String mainCommandPerm = "";
    public boolean createCooldown = true;
    public boolean bankWithdrawing = true;
    public boolean islandShop = true;
    public boolean automaticUpdate = true;
    public boolean defaultIslandPublic = true;
    public boolean netherIslands = true;
    public boolean publicNetherPortals = true;
    public boolean forceShortPortalRadius = true;
    public boolean islandMenu = true;
    public boolean voidTeleport = true;
    public boolean notifyAvailableUpdate = true;
    public boolean clearInventories = false;
    public boolean clearEnderChests = false;
    public boolean restartUpgradesOnRegen = true;
    public boolean allowWaterInNether = true;
    public boolean createIslandonHome = true;
    public boolean allowExplosions = true;
    public boolean disablePvPBetweenIslandMembers = true;
    public boolean disablePvPOnIslands = true;
    public boolean allowMobGuestTargeting = true;
    public boolean disableBypassOnJoin = true;
    public boolean displayNumberAbbreviations = true;
    public boolean prettierAbbreviations = true;
    public boolean logTransactions = true;
    public boolean logBankBalanceChange = true;
    public boolean keepInventoryOnVoid = true;
    public boolean createIslandOnJoin = false;
    public boolean ignoreCooldownOnJoinCreation = false;
    public boolean enableBlockStacking = true;
    public boolean stripTopIslandPlaceholderColors = true;
    public boolean denyNaturalSpawnWhitelist = false;
    public boolean netherLavaOcean = true;
    public int deleteBackupsAfterDays = 7;
    public int regenCooldown = 3600;
    public int distance = 151;
    public int maxIslandName = 16;
    public int minIslandName = 3;
    public int valueUpdateInterval = 20 * 30;
    public int playersOnIslandRefreshTime = 15;
    public int intervalBetweenMobTarget = 15;
    public int islandMoneyPerValue = 1000;
    public int numberAbbreviationDecimalPlaces = 2;
    public int minOceanFloorLevel = 10;
    public int maxOceanFloorLevel = 25;
    public int waterHeight = 80;
    public double valuePerLevel = 100.00;
    public double dailyMoneyInterest = 0.5;
    public double dailyCrystalsInterest = 5;
    public double dailyExpInterest = 0.01;
    public XBiome defaultBiome = XBiome.BEACH;
    public XBiome defaultNetherBiome = XBiome.NETHER_WASTES;
    public MissionRestart missionRestart = MissionRestart.Daily;
    public String worldSpawn = "world";
    public Map<Role, Permissions> defaultPermissions = new HashMap<Role, Permissions>() {{
        for (Role role : Role.values()) {
            if (role == Role.Visitor) {
                put(role, new Permissions(false, false, false, false, false, false, false, false, false, true, true, false, false, false, false, false));
            } else {
                put(role, new Permissions());
            }
        }
    }};
    public Map<Integer, Integer> islandTopSlots = new HashMap<Integer, Integer>() {{
        put(1, 4);
        put(2, 12);
        put(3, 14);
        put(4, 19);
        put(5, 20);
        put(6, 21);
        put(7, 22);
        put(8, 23);
        put(9, 24);
        put(10, 25);
    }};

    public Map<XMaterial, Integer> limitedBlocks = new HashMap<XMaterial, Integer>() {{
        put(XMaterial.HOPPER, 50);
        put(XMaterial.SPAWNER, 10);
    }};

    public Map<XMaterial, Double> blockvalue = null;
    public Map<String, Double> spawnervalue = null;
    public List<XBiome> biomes = null;
    public Map<XBiome, BiomeConfig> islandBiomes = new HashMap<XBiome, BiomeConfig>() {{
        for (XBiome biome : XBiome.VALUES.stream().filter(biome -> !biome.equals(XBiome.THE_VOID)).collect(Collectors.toList())) {
            put(biome, new BiomeConfig());
        }
    }};
    public List<EntityType> blockedEntities = Arrays.asList(EntityType.PRIMED_TNT, EntityType.MINECART_TNT, EntityType.FIREBALL, EntityType.SMALL_FIREBALL, EntityType.ENDER_PEARL);

    public List<EntityType> denyNaturalSpawn = Collections.emptyList();

    public static class BiomeConfig {
        public double price = 5000.0;
        public int crystals = 5;
        public XMaterial icon = XMaterial.GRASS_BLOCK;
    }

}

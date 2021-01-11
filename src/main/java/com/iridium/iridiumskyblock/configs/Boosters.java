package com.iridium.iridiumskyblock.configs;

import com.cryptomorin.xseries.XMaterial;

import java.util.Arrays;

public class Boosters {

    public Booster islandSpawnerBooster = new Booster("spawner", 15, 0, 3600, true, new Inventories.Item(XMaterial.SPAWNER, 10, 1, "&b&lIncreased Mobs", Arrays.asList("&7Are your spawners too slow? Buy this", "&7booster and increase spawner rates x2.", "", "&b&lInformation:", "&b&l * &7Time Remaining: &b{spawnerbooster_minutes} minutes and {spawnerbooster_seconds}seconds", "&b&l * &7Booster Cost: &b{spawnerbooster_crystalcost} Crystals and ${spawnerbooster_vaultcost}", "", "&b&l[!] &bRight Click to Purchase this Booster.")));
    public Booster islandFarmingBooster = new Booster("farming", 15, 0, 3600, true, new Inventories.Item(XMaterial.WHEAT, 12, 1, "&b&lIncreased Crops", Arrays.asList("&7Are your crops too slow? Buy this", "&7booster and increase crop growth rates x2.", "", "&b&lInformation:", "&b&l * &7Time Remaining: &b{farmingbooster_minutes} minutes and {farmingbooster_seconds}seconds", "&b&l * &7Booster Cost: &b{farmingbooster_crystalcost} Crystals and ${farmingbooster_vaultcost}", "", "&b&l[!] &bRight Click to Purchase this Booster.")));
    public Booster islandExperienceBooster = new Booster("experience", 15, 0, 3600, true, new Inventories.Item(XMaterial.EXPERIENCE_BOTTLE, 14, 1, "&b&lIncreased Experience", Arrays.asList("&7Takes too long to get experience? Buy this", "&7booster and experience rates x2.", "", "&b&lInformation:", "&b&l * &7Time Remaining: &b{experiencebooster_minutes} minutes and {experiencebooster_seconds}seconds", "&b&l * &7Booster Cost: &b{experiencebooster_crystalcost} Crystals and ${experiencebooster_vaultcost}", "", "&b&l[!] &bRight Click to Purchase this Booster.")));
    public Booster islandFlightBooster = new Booster("flight", 15, 0, 3600, true, new Inventories.Item(XMaterial.FEATHER, 16, 1, "&b&lIncreased Flight", Arrays.asList("&7Tired of falling off your island? Buy this", "&7booster and allow all members to fly.", "", "&b&lInformation:", "&b&l * &7Time Remaining: &b{flightbooster_minutes} minutes and {flightbooster_seconds}seconds", "&b&l * &7Booster Cost: &b{flightbooster_crystalcost} Crystals and ${flightbooster_vaultcost}", "", "&b&l[!] &bRight Click to Purchase this Booster.")));

    public static class Booster {
        public String name;
        public int crystalsCost;
        public int vaultCost;
        public int time;
        public boolean enabled;
        public Inventories.Item item;

        public Booster(String name, int crystalsCost, int vaultCost, int time, boolean enabled, Inventories.Item item) {
            this.name = name;
            this.crystalsCost = crystalsCost;
            this.vaultCost = vaultCost;
            this.time = time;
            this.enabled = enabled;
            this.item = item;
        }
    }
}

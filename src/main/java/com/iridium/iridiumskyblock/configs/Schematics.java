package com.iridium.iridiumskyblock.configs;

import com.cryptomorin.xseries.XBiome;
import com.cryptomorin.xseries.XMaterial;

import java.util.Collections;
import java.util.List;

public class Schematics {
    public List<LegacyFakeSchematic> schematics = null;
    public List<FakeSchematic> schematicList = Collections.singletonList(
            new FakeSchematic(-0.5, 80, -2.5, new SchematicData("island.schem", XBiome.BEACH, 0.0, -11, 0.0), new SchematicData("nether.schem", XBiome.NETHER_WASTES, 0.0, -20, 0.0), "", XMaterial.GRASS_BLOCK, "&b&lDefault Island", Collections.singletonList("&7The default island"), 0)
    );

    public static class LegacyFakeSchematic {
        public String name;
        public double x;
        public double y;
        public double z;
        public double xOffset;
        public double yOffset;
        public double zOffset;
        public double xNetherOffset;
        public double yNetherOffset;
        public double zNetherOffset;
        public String netherisland;
        public String permission;
        public XMaterial item;
        public XBiome biome;
        public String displayname;
        public List<String> lore;
        public Integer slot;

        public LegacyFakeSchematic(String name, String netherisland, double x, double y, double z, double xOffset, double yOffset, double zOffset, double xNetherOffset, double yNetherOffset, double zNetherOffset, String permission, XMaterial item, XBiome biome, String displayname, List<String> lore, int slot) {
            this.name = name;
            this.netherisland = netherisland;
            this.permission = permission;
            this.item = item;
            this.displayname = displayname;
            this.lore = lore;
            this.biome = biome;
            this.x = x;
            this.y = y;
            this.z = z;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
            this.xNetherOffset = xNetherOffset;
            this.yNetherOffset = yNetherOffset;
            this.zNetherOffset = zNetherOffset;
            this.slot = slot;
        }

        public FakeSchematic tonew() {
            return new FakeSchematic(x, y, z, new SchematicData(name, biome, xOffset, yOffset, zOffset), new SchematicData(netherisland, XBiome.NETHER_WASTES, xNetherOffset, yNetherOffset, zNetherOffset), permission, item, displayname, lore, slot);
        }
    }

    public static class FakeSchematic {
        public double x;
        public double y;
        public double z;
        public SchematicData overworldData;
        public SchematicData netherData;
        public String permission;
        public XMaterial item;
        public String displayname;
        public List<String> lore;
        public Integer slot;

        public FakeSchematic(double x, double y, double z, SchematicData overworldData, SchematicData netherData, String permission, XMaterial item, String displayname, List<String> lore, int slot) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.overworldData = overworldData;
            this.netherData = netherData;
            this.permission = permission;
            this.item = item;
            this.displayname = displayname;
            this.lore = lore;
            this.slot = slot;
        }
    }

    public static class SchematicData {
        public String schematic;
        public XBiome biome;
        public double xOffset;
        public double yOffset;
        public double zOffset;

        public SchematicData(String schematic, XBiome biome, double xOffset, double yOffset, double zOffset) {
            this.schematic = schematic;
            this.biome = biome;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
        }
    }
}

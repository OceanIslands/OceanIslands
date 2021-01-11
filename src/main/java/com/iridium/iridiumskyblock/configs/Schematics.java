package com.iridium.iridiumskyblock.configs;

import com.cryptomorin.xseries.XBiome;
import com.cryptomorin.xseries.XMaterial;

import java.util.Collections;
import java.util.List;

public class Schematics {
    public List<FakeSchematic> schematicList = Collections.singletonList(
            new FakeSchematic(-0.5, 80, -2.5, new SchematicData("island.schem", XBiome.BEACH, 0.0, -11, 0.0), new SchematicData("nether.schem", XBiome.NETHER_WASTES, 0.0, -20, 0.0), "", XMaterial.GRASS_BLOCK, "&b&lDefault Island", Collections.singletonList("&7The default island"), 0)
    );

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

package com.iridium.iridiumskyblock;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.configs.Config;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SkyblockGenerator extends ChunkGenerator {

    public byte[][] blockSections;

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int cx, int cz, @NotNull BiomeGrid biomeGrid) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        final ChunkData chunkData = createChunkData(world);
        generator.setScale(0.005D);

        Biome biome;
        final Config config = IridiumSkyblock.getConfiguration();
        final String worldName = world.getName();
        if (worldName.equals(config.worldName))
            biome = config.defaultBiome.getBiome();
        else if (worldName.equals(config.netherWorldName))
            biome = config.defaultNetherBiome.getBiome();
        else
            return chunkData;
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                biomeGrid.setBiome(x, z, biome);
            }
        }

        for (int x = 0; x < 16; x++) {
          for (int z = 0; z < 16; z++) {
            int currentFloorHeight = (int) ((generator.noise(cx * 16 + x, cz * 16 + z, 0.5D, 0.5D, true) + 1) * (config.maxOceanFloorLevel - config.minOceanFloorLevel) + config.minOceanFloorLevel);
            chunkData.setBlock(x, 0, z,
              Objects.requireNonNull(XMaterial.BEDROCK.parseMaterial())
            );
            for (int y = 1; y < currentFloorHeight; y++) {
              chunkData.setBlock(x, y, z,
                Objects.requireNonNull(XMaterial.GRAVEL.parseMaterial())
              );
            }
            chunkData.setBlock(x, currentFloorHeight, z,
              Objects.requireNonNull(XMaterial.SAND.parseMaterial())
            );
            for (int y = currentFloorHeight + 1; y <= config.waterHeight; y++) {
              XMaterial material = config.netherLavaOcean && world.getEnvironment() == Environment.NETHER ? XMaterial.LAVA : XMaterial.WATER;
              chunkData.setBlock(x, y, z, Objects.requireNonNull(material.parseMaterial()));
            }
            for (int y = config.waterHeight + 1; y <= 255; y++) {
              XMaterial material = XMaterial.AIR;
              chunkData.setBlock(x, y, z, Objects.requireNonNull(material.parseMaterial()));
            }
          }
        }

        return chunkData;
    }

    public byte[][] generateBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
        if (blockSections == null) {
            blockSections = new byte[world.getMaxHeight() / 16][];
        }
        return blockSections;
    }

    @Override
    public boolean canSpawn(@NotNull World world, int x, int z) {
        return true;
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
        return Collections.emptyList();
    }

}
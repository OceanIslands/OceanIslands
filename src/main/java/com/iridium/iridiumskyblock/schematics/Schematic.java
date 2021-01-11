package com.iridium.iridiumskyblock.schematics;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonParser;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.jnbt.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Schematic implements WorldEdit {

    public HashMap<String, SchematicData> schematicData = new HashMap<>();

    public boolean ISFLAT = XMaterial.supports(13);

    @Override
    public int version() {
        return 0;
    }

    @Override
    public void paste(File file, Location location, Island island) {
        SchematicData schematicData = getSchematicData(file);
        short length = schematicData.length;
        short width = schematicData.width;
        short height = schematicData.height;
        location.subtract(width / 2.00, height / 2.00, length / 2.00); // Centers the schematic
        if (schematicData.schematicVersion == SchematicData.SchematicVersion.v_1_8) {

            byte[] blocks = schematicData.blocks;
            byte[] blockData = schematicData.data;

            //LoadBlocks
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    for (int z = 0; z < length; ++z) {
                        int index = y * width * length + z * width + x;
                        Block block = new Location(location.getWorld(), x + location.getX(), y + location.getY(), z + location.getZ()).getBlock();
                        IridiumSkyblock.getNms().setBlockFast(block, blocks[index], blockData[index]);
                    }
                }
            }
            //Tile Entities
            if (schematicData.tileEntities != null) {
                for (Tag tag : schematicData.tileEntities) {
                    if (!(tag instanceof CompoundTag))
                        continue;
                    CompoundTag t = (CompoundTag) tag;
                    Map<String, Tag> tags = t.getValue();

                    int x = SchematicData.getChildTag(tags, "x", IntTag.class).getValue();
                    int y = SchematicData.getChildTag(tags, "y", IntTag.class).getValue();
                    int z = SchematicData.getChildTag(tags, "z", IntTag.class).getValue();
                    Block block = new Location(location.getWorld(), x + location.getX(), y + location.getY(), z + location.getZ()).getBlock();

                    String id = SchematicData.getChildTag(tags, "id", StringTag.class).getValue().toLowerCase().replace("minecraft:", "");
                    if (id.equalsIgnoreCase("chest")) {
                        List<Tag> items = SchematicData.getChildTag(tags, "Items", ListTag.class).getValue();
                        if (block.getState() instanceof Chest) {
                            Chest chest = (Chest) block.getState();
                            for (Tag item : items) {
                                if (!(item instanceof CompoundTag))
                                    continue;
                                Map<String, Tag> itemtag = ((CompoundTag) item).getValue();
                                byte slot = SchematicData.getChildTag(itemtag, "Slot", ByteTag.class).getValue();
                                String name = (SchematicData.getChildTag(itemtag, "id", StringTag.class).getValue()).toLowerCase().replace("minecraft:", "").replace("reeds", "sugar_cane");
                                Byte amount = SchematicData.getChildTag(itemtag, "Count", ByteTag.class).getValue();
                                short damage = SchematicData.getChildTag(itemtag, "Damage", ShortTag.class).getValue();
                                if (name.equalsIgnoreCase("grass")) name = "GRASS_BLOCK";
                                Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(name.toUpperCase());
                                if (optionalXMaterial.isPresent()) {
                                    XMaterial material = optionalXMaterial.get();
                                    ItemStack itemStack = material.parseItem();
                                    if (itemStack != null) {
                                        itemStack.setAmount(amount);
                                        chest.getBlockInventory().setItem(slot, itemStack);
                                    }
                                }
                            }
                        }
                    } else if (id.equalsIgnoreCase("sign")) {
                        if (block.getState() instanceof Sign) {
                            Sign sign = (Sign) block.getState();
                            JsonParser parser = new JsonParser();
                            String line1 = parser.parse(SchematicData.getChildTag(tags, "Text1", StringTag.class).getValue()).getAsString().isEmpty() ? "" : parser.parse(SchematicData.getChildTag(tags, "Text1", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString().replace("[ISLAND_OWNER]", User.getUser(island.owner).name);
                            String line2 = parser.parse(SchematicData.getChildTag(tags, "Text2", StringTag.class).getValue()).getAsString().isEmpty() ? "" : parser.parse(SchematicData.getChildTag(tags, "Text2", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString().replace("[ISLAND_OWNER]", User.getUser(island.owner).name);
                            String line3 = parser.parse(SchematicData.getChildTag(tags, "Text3", StringTag.class).getValue()).getAsString().isEmpty() ? "" : parser.parse(SchematicData.getChildTag(tags, "Text3", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString().replace("[ISLAND_OWNER]", User.getUser(island.owner).name);
                            String line4 = parser.parse(SchematicData.getChildTag(tags, "Text4", StringTag.class).getValue()).getAsString().isEmpty() ? "" : parser.parse(SchematicData.getChildTag(tags, "Text4", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString().replace("[ISLAND_OWNER]", User.getUser(island.owner).name);
                            if (!parser.parse(SchematicData.getChildTag(tags, "Text1", StringTag.class).getValue()).getAsString().isEmpty() && parser.parse(SchematicData.getChildTag(tags, "Text1", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().has("color"))
                                line1 = ChatColor.valueOf(parser.parse(SchematicData.getChildTag(tags, "Text1", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("color").getAsString().toUpperCase()) + line1;
                            if (!parser.parse(SchematicData.getChildTag(tags, "Text2", StringTag.class).getValue()).getAsString().isEmpty() && parser.parse(SchematicData.getChildTag(tags, "Text2", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().has("color"))
                                line2 = ChatColor.valueOf(parser.parse(SchematicData.getChildTag(tags, "Text2", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("color").getAsString().toUpperCase()) + line2;
                            if (!parser.parse(SchematicData.getChildTag(tags, "Text3", StringTag.class).getValue()).getAsString().isEmpty() && parser.parse(SchematicData.getChildTag(tags, "Text3", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().has("color"))
                                line3 = ChatColor.valueOf(parser.parse(SchematicData.getChildTag(tags, "Text3", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("color").getAsString().toUpperCase()) + line3;
                            if (!parser.parse(SchematicData.getChildTag(tags, "Text4", StringTag.class).getValue()).getAsString().isEmpty() && parser.parse(SchematicData.getChildTag(tags, "Text4", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().has("color"))
                                line4 = ChatColor.valueOf(parser.parse(SchematicData.getChildTag(tags, "Text4", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("color").getAsString().toUpperCase()) + line4;
                            sign.setLine(0, line1);
                            sign.setLine(1, line2);
                            sign.setLine(2, line3);
                            sign.setLine(3, line4);
                            sign.update(true);
                        }
                    }
                }
            }
        } else {
            //LoadBlocks
            if (ISFLAT) {
                    for (int x = 0; x < width; ++x) {
                        for (int y = 0; y < height; ++y) {
                            for (int z = 0; z < length; ++z) {
                                int index = y * width * length + z * width + x;
                                Block block = new Location(location.getWorld(), x + location.getX(), y + location.getY(), z + location.getZ()).getBlock();
                                for (String s : schematicData.palette.keySet()) {
                                    int i = SchematicData.getChildTag(schematicData.palette, s, IntTag.class).getValue();
                                    if (schematicData.blockdata[index] == i) {
                                        block.setBlockData(Bukkit.createBlockData(s), false);
                                    }
                                }
                            }
                        }
                    }
                    if (schematicData.version == 2) {
                        //Tile Entities
                        if (schematicData.tileEntities != null) {
                            for (Tag tag : schematicData.tileEntities) {
                                if (!(tag instanceof CompoundTag))
                                    continue;
                                CompoundTag t = (CompoundTag) tag;
                                Map<String, Tag> tags = t.getValue();

                                int[] pos = SchematicData.getChildTag(tags, "Pos", IntArrayTag.class).getValue();

                                int x = pos[0];
                                int y = pos[1];
                                int z = pos[2];

                                Block block = new Location(location.getWorld(), x + location.getX(), y + location.getY(), z + location.getZ()).getBlock();
                                String id = SchematicData.getChildTag(tags, "Id", StringTag.class).getValue().toLowerCase().replace("minecraft:", "");
                                if (id.equalsIgnoreCase("chest")) {
                                    List<Tag> items = SchematicData.getChildTag(tags, "Items", ListTag.class).getValue();
                                    if (block.getState() instanceof Chest) {
                                        Chest chest = (Chest) block.getState();
                                        for (Tag item : items) {
                                            if (!(item instanceof CompoundTag))
                                                continue;
                                            Map<String, Tag> itemtag = ((CompoundTag) item).getValue();
                                            byte slot = SchematicData.getChildTag(itemtag, "Slot", ByteTag.class).getValue();
                                            String name = (SchematicData.getChildTag(itemtag, "id", StringTag.class).getValue()).toLowerCase().replace("minecraft:", "").replace("reeds", "sugar_cane");
                                            Byte amount = SchematicData.getChildTag(itemtag, "Count", ByteTag.class).getValue();
                                            Optional<XMaterial> optionalXMaterial = XMaterial.matchXMaterial(name.toUpperCase());
                                            if (optionalXMaterial.isPresent()) {
                                                XMaterial material = optionalXMaterial.get();
                                                ItemStack itemStack = material.parseItem();
                                                if (itemStack != null) {
                                                    itemStack.setAmount(amount);
                                                    chest.getBlockInventory().setItem(slot, itemStack);
                                                }
                                            }
                                        }
                                    }
                                } else if (id.equalsIgnoreCase("sign")) {
                                    if (block.getState() instanceof Sign) {
                                        Sign sign = (Sign) block.getState();
                                        JsonParser parser = new JsonParser();
                                        String line1 = parser.parse(SchematicData.getChildTag(tags, "Text1", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString().replace("[ISLAND_OWNER]", User.getUser(island.owner).name);
                                        String line2 = parser.parse(SchematicData.getChildTag(tags, "Text2", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString().replace("[ISLAND_OWNER]", User.getUser(island.owner).name);
                                        String line3 = parser.parse(SchematicData.getChildTag(tags, "Text3", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString().replace("[ISLAND_OWNER]", User.getUser(island.owner).name);
                                        String line4 = parser.parse(SchematicData.getChildTag(tags, "Text4", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString().replace("[ISLAND_OWNER]", User.getUser(island.owner).name);
                                        if (parser.parse(SchematicData.getChildTag(tags, "Text1", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().has("color"))
                                            line1 = ChatColor.valueOf(parser.parse(SchematicData.getChildTag(tags, "Text1", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("color").getAsString().toUpperCase()) + line1;
                                        if (parser.parse(SchematicData.getChildTag(tags, "Text2", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().has("color"))
                                            line2 = ChatColor.valueOf(parser.parse(SchematicData.getChildTag(tags, "Text2", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("color").getAsString().toUpperCase()) + line2;
                                        if (parser.parse(SchematicData.getChildTag(tags, "Text3", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().has("color"))
                                            line3 = ChatColor.valueOf(parser.parse(SchematicData.getChildTag(tags, "Text3", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("color").getAsString().toUpperCase()) + line3;
                                        if (parser.parse(SchematicData.getChildTag(tags, "Text4", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().has("color"))
                                            line4 = ChatColor.valueOf(parser.parse(SchematicData.getChildTag(tags, "Text4", StringTag.class).getValue()).getAsJsonObject().get("extra").getAsJsonArray().get(0).getAsJsonObject().get("color").getAsString().toUpperCase()) + line4;
                                        sign.setLine(0, line1);
                                        sign.setLine(1, line2);
                                        sign.setLine(2, line3);
                                        sign.setLine(3, line4);
                                        sign.update(true);
                                    }
                                }
                            }
                        }
                    }
            } else {
                location.getBlock().setType(Material.STONE);
                IridiumSkyblock.getInstance().getLogger().warning("Tried to load a 1.13+ schematic in a native minecraft version");
            }
        }
    }

    public SchematicData getSchematicData(File file) {
        if (schematicData.containsKey(file.getAbsolutePath())) {
            return schematicData.get(file.getAbsolutePath());
        }
        SchematicData data = null;
        try {
            data = SchematicData.loadSchematic(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        schematicData.put(file.getAbsolutePath(), data);
        return data;
    }
}

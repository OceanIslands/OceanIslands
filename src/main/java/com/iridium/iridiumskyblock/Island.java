package com.iridium.iridiumskyblock;

import com.cryptomorin.xseries.XBiome;
import com.cryptomorin.xseries.XMaterial;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.spawn.EssentialsSpawn;
import com.iridium.iridiumskyblock.api.*;
import com.iridium.iridiumskyblock.configs.*;
import com.iridium.iridiumskyblock.configs.Missions.Mission;
import com.iridium.iridiumskyblock.configs.Missions.MissionData;
import com.iridium.iridiumskyblock.gui.*;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import com.iridium.iridiumskyblock.support.SpawnerSupport;
import java.util.function.Consumer;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class Island {

    public int id;
    public String owner;
    public Set<String> members;
    public Location pos1;
    public Location pos2;
    public Location center;
    public Location home;

    public transient UpgradeGUI upgradeGUI;
    public transient BoosterGUI boosterGUI;
    public transient MissionsGUI missionsGUI;
    public transient MembersGUI membersGUI;
    public transient WarpGUI warpGUI;
    public transient BorderColorGUI borderColorGUI;
    public transient SchematicSelectGUI schematicSelectGUI;
    public transient PermissionsGUI permissionsGUI;
    public transient IslandMenuGUI islandMenuGUI;
    public transient IslandAdminGUI islandAdminGUI;
    public transient CoopGUI coopGUI;
    public transient BankGUI bankGUI;
    public transient MultiplePagesGUI<BiomeGUI> biomeGUI;
    public transient MultiplePagesGUI<BiomeGUI> netherBiomeGUI;
    public transient VisitorGUI visitorGUI;

    private transient int boosterId;

    private HashMap<String, Integer> boosterTimes = new HashMap<>();
    private HashMap<String, Integer> upgradeLevels = new HashMap<>();
    private HashMap<String, Double> bankItems = new HashMap<>();

    public transient int generateID;

    public double value;

    private double lastMissionValue;

    public double extravalue;

    public transient ConcurrentHashMap<String, Integer> valuableBlocks;
    public transient ConcurrentHashMap<String, Integer> spawners;
    public ConcurrentHashMap<Location, Integer> stackedBlocks;

    public List<IslandWarp> islandWarps;

    private double startvalue;

    private Map<String, Integer> missions = new HashMap<>();

    private Map<String, Integer> missionLevels = new HashMap<>();

    public boolean visit;

    public Color borderColor;

    private Map<Role, Permissions> permissions;

    public String schematic;
    public String netherschematic;

    private Set<String> bans;

    private Set<String> votes;

    private Set<Integer> coop;

    public transient Set<Integer> coopInvites;

    public String name;

    public XBiome biome;

    public XBiome netherBiome;

    public transient Set<Location> failedGenerators;
    public transient int interestCrystal;
    public transient int interestExp;
    public transient double interestMoney;

    private Date lastRegen;

    private transient Set<Player> playersOnIsland;
    private long lastPlayerCaching;

    private static final transient boolean IS_FLAT = XMaterial.supports(13);
    private static transient Method getMaterial;
    private static transient Method getBlock;

    static {
        try {
            getMaterial = Material.class.getMethod("getMaterial", int.class);
            getBlock = ChunkSnapshot.class.getMethod("getBlockTypeId", int.class, int.class, int.class);
        } catch (NoSuchMethodException e) {
            getMaterial = null;
            getBlock = null;
        }
    }

    public Island(Player owner, Location pos1, Location pos2, Location center, Location home, int id) {
        User user = User.getUser(owner);
        user.role = Role.Owner;
        this.biome = IridiumSkyblock.getConfiguration().defaultBiome;
        this.netherBiome = XBiome.NETHER_WASTES;
        valuableBlocks = new ConcurrentHashMap<>();
        spawners = new ConcurrentHashMap<>();
        this.owner = user.player;
        this.name = user.name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.center = center;
        this.home = home;
        this.members = new HashSet<>(Collections.singletonList(user.player));
        this.id = id;
        value = 0;
        lastMissionValue = 0;
        islandWarps = new ArrayList<>();
        startvalue = -1;
        borderColor = IridiumSkyblock.getBorder().startingColor;
        visit = IridiumSkyblock.getConfiguration().defaultIslandPublic;
        permissions = new HashMap<>(IridiumSkyblock.getConfiguration().defaultPermissions);
        this.coop = new HashSet<>();
        this.bans = new HashSet<>();
        this.votes = new HashSet<>();
        init();
        Bukkit.getPluginManager().callEvent(new IslandCreateEvent(owner, this));
    }

    public void initBlocks() {
        if (!center.getWorld().isChunkLoaded(center.getBlockX() >> 4, center.getBlockZ() >> 4)) return;
        final IridiumSkyblock plugin = IridiumSkyblock.getInstance();
        final boolean nether = IridiumSkyblock.getConfiguration().netherIslands;

        int minX = pos1.getChunk().getX();
        int minZ = pos1.getChunk().getZ();
        int maxX = pos2.getChunk().getX();
        int maxZ = pos2.getChunk().getZ();

        valuableBlocks.clear();
        spawners.clear();

        for (Location location : stackedBlocks.keySet()) {
            XMaterial xMaterial = XMaterial.matchXMaterial(location.getBlock().getType());
            valuableBlocks.compute(xMaterial.name(), (xmaterialName, original) -> {
                if (original == null) return stackedBlocks.get(location) - 1;
                return original + (stackedBlocks.get(location) - 1);
            });
            if (xMaterial == XMaterial.AIR || xMaterial == XMaterial.CAVE_AIR || xMaterial == XMaterial.VOID_AIR) {
                stackedBlocks.remove(location);
            }
        }
        Bukkit.getScheduler().runTask(IridiumSkyblock.getInstance(), (Runnable) this::sendHolograms);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                //Update the nether world values
                if (nether) {
                    Chunk netherChunk = IslandManager.getNetherWorld().getChunkAt(x, z);
                    computeValue(plugin, x, z, netherChunk);
                }

                //Update overworld values
                Chunk chunk = IslandManager.getWorld().getChunkAt(x, z);
                computeValue(plugin, x, z, chunk);
            }
        }
        Bukkit.getScheduler().runTaskLater(IridiumSkyblock.getInstance(), this::calculateIslandValue, 20);
    }

    private void computeValue(IridiumSkyblock plugin, int finalX, int finalZ, Chunk chunk) {
        ChunkSnapshot netherSnapshot = chunk.getChunkSnapshot(true, false, false);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (int x1 = 0; x1 < 16; x1++) {
                for (int z1 = 0; z1 < 16; z1++) {
                    if (!isInIsland(x1 + (16 * finalX), z1 + (16 * finalZ)))
                        continue;
                    final int maxy = netherSnapshot.getHighestBlockYAt(x1, z1);
                    for (int y = 0; y < maxy; y++) {
                        final Material material;
                        if (IS_FLAT) {
                            material = netherSnapshot.getBlockType(x1, y, z1);
                        } else {
                            try {
                                material = (Material) getMaterial.invoke(null, getBlock.invoke(
                                        netherSnapshot, x1, y, z1));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        final XMaterial xMaterial = XMaterial.matchXMaterial(material);
                        if (Utils.isBlockValuable(xMaterial)) {
                            valuableBlocks.compute(xMaterial.name(), (xmaterialName, original) -> {
                                if (original == null) return 1;
                                return original + 1;
                            });
                        }
                    }
                }
            }
        });
    }

    public void resetMissions() {
        if (missions == null) missions = new HashMap<>();
        if (missionLevels == null) missionLevels = new HashMap<>();
        missions.clear();
        missionLevels.clear();
        lastMissionValue = value;
    }

    public int getMission(String mission) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        return missions.get(mission);
    }

    public void addMission(String mission, int amount) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        if (missions.get(mission) == Integer.MIN_VALUE) return;
        missions.put(mission, missions.get(mission) + amount);
        checkMissionStatus(mission);
    }

    public void setMission(String mission, int amount) {
        if (missions == null) missions = new HashMap<>();
        if (!missions.containsKey(mission)) missions.put(mission, 0);
        if (missions.get(mission) == Integer.MIN_VALUE) return;
        missions.put(mission, amount);
        checkMissionStatus(mission);
    }

    private void checkMissionStatus(String mission) {
        for (Mission m : IridiumSkyblock.getMissions().missions) {
            if (m.name.equalsIgnoreCase(mission)) {
                if (!getMissionLevels().containsKey(mission)) getMissionLevels().put(mission, 1);
                if (m.levels.get(getMissionLevels().get(mission)).amount <= missions.get(mission)) {
                    completeMission(mission);
                }
            }
        }
    }

    public Permissions getPermissions(User user) {
        if (user.bypassing) return new Permissions();

        Role role;
        if (user.islandID == id) role = user.getRole();
        else if (isCoop(user.getIsland())) role = Role.Member;
        else role = Role.Visitor;
        return getPermissions(role);
    }

    public Permissions getPermissions(Role role) {
        if (permissions == null)
            permissions = new HashMap<>(IridiumSkyblock.getConfiguration().defaultPermissions);
        if (!permissions.containsKey(role)) {
            permissions.put(role, new Permissions());
        }
        return permissions.get(role);
    }

    public void setBorderColor(Color color) {
        borderColor = color;
        sendBorder();
    }

    public void sendBorder() {
        for (Player p : getPlayersOnIsland()) {
            sendBorder(p);
        }
    }

    public void sendBorder(Player p) {
        double size = IridiumSkyblock.getUpgrades().islandSizeUpgrade.getIslandUpgrade(getSizeLevel()).size;
        if (size % 2 == 0) size++;
        String worldName = p.getLocation().getWorld().getName();
        if (worldName.equals(IslandManager.getWorld().getName())) {
            IridiumSkyblock.getNms().sendWorldBorder(p, borderColor, size, center);
        } else if (IridiumSkyblock.getConfiguration().netherIslands && worldName
                .equals(IslandManager.getNetherWorld().getName())) {
            Location loc = center.clone();
            loc.setWorld(IslandManager.getNetherWorld());
            IridiumSkyblock.getNms().sendWorldBorder(p, borderColor, size, loc);
        }
    }

    public void hideBorder() {
        for (Player p : getPlayersOnIsland()) {
            hideBorder(p);
        }
    }

    public void hideBorder(Player p) {
        IridiumSkyblock.getNms().sendWorldBorder(p, borderColor, Integer.MAX_VALUE, center.clone());
    }

    public void completeMission(String missionName) {
        missionLevels.putIfAbsent(missionName, 1);

        final Config config = IridiumSkyblock.getConfiguration();
        missions.put(missionName, (config.missionRestart == MissionRestart.Instantly ? 0 : Integer.MIN_VALUE));

        final Mission mission = IridiumSkyblock
                .getMissions()
                .missions
                .stream()
                .filter(m -> m.name.equalsIgnoreCase(missionName))
                .findAny()
                .orElse(null);
        if (mission == null) return;

        final Map<Integer, MissionData> levels = mission.levels;
        final int levelProgress = missionLevels.get(missionName);
        final MissionData level = levels.get(levelProgress);
        final int crystalReward = level.crystalReward;
        final int vaultReward = level.vaultReward;
        setCrystals(getCrystals() + crystalReward);
        setMoney(getMoney() + vaultReward);
        Bukkit.getPluginManager().callEvent(new MissionCompleteEvent(this, missionName, level.type, levelProgress));
        final Messages messages = IridiumSkyblock.getMessages();
        final String titleMessage = messages.missionComplete
                .replace("%mission%", missionName)
                .replace("%level%", levelProgress + "");
        final String subTitleMessage = messages.rewards
                .replace("%crystalsReward%", crystalReward + "")
                .replace("%vaultReward%", vaultReward + "");
        for (String member : members) {
            final User user = User.getUser(member);
            final Player p = Bukkit.getPlayer(user.name);
            if (p == null) continue;
            IridiumSkyblock.getNms().sendTitle(p, titleMessage, 20, 40, 20);
            IridiumSkyblock.getNms().sendSubTitle(p, subTitleMessage, 20, 40, 20);
        }

        //Reset current mission status
        if (mission.levels.containsKey(levelProgress + 1)) {
            //We have another mission, put us on the next level
            missions.remove(missionName);
            missionLevels.put(missionName, levelProgress + 1);
        } else if (config.missionRestart == MissionRestart.Instantly) {
            missions.remove(missionName);
            missionLevels.remove(missionName);
        }
    }

    public void calculateIslandValue() {
        if (!center.getWorld().isChunkLoaded(center.getBlockX() >> 4, center.getBlockZ() >> 4)) return;
        if (valuableBlocks == null) valuableBlocks = new ConcurrentHashMap<>();
        if (spawners == null) spawners = new ConcurrentHashMap<>();

        final BlockValues blockValues = IridiumSkyblock.getBlockValues();
        final Map<XMaterial, Double> blockValueMap = blockValues.blockvalue;

        BigDecimal value = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : valuableBlocks.entrySet()) {
            final String item = entry.getKey();
            final BigDecimal amount = BigDecimal.valueOf(entry.getValue());
            if (amount.intValue() < 1) continue;
            final Optional<XMaterial> xmaterial = XMaterial.matchXMaterial(item);
            if (!xmaterial.isPresent()) continue;

            if (!blockValueMap.containsKey(xmaterial.get())) continue;
            final BigDecimal blockValue = BigDecimal.valueOf(blockValueMap.get(xmaterial.get()));

            value = value.add(blockValue.multiply(amount));
        }

        final Config config = IridiumSkyblock.getConfiguration();

        final Set<World> worlds = new HashSet<>();
        final World islandWorld = IslandManager.getWorld();
        worlds.add(islandWorld);

        if (config.netherIslands) {
            final World netherIslandWorld = IslandManager.getNetherWorld();
            worlds.add(netherIslandWorld);
        }

        final Chunk pos1Chunk = pos1.getChunk();
        final int minChunkX = pos1Chunk.getX();
        final int minChunkZ = pos1Chunk.getZ();

        final Chunk pos2Chunk = pos2.getChunk();
        final int maxChunkX = pos2Chunk.getX();
        final int maxChunkZ = pos2Chunk.getZ();

        final double minX = pos1.getX();
        final double minZ = pos1.getZ();
        final double maxX = pos2.getX();
        final double maxZ = pos2.getZ();

        final Map<String, Double> spawnerValueMap = blockValues.spawnervalue;

        Function<CreatureSpawner, Integer> getSpawnerAmount;
        SpawnerSupport spawnerSupport = IridiumSkyblock.getInstance().getSpawnerSupport();
        if (spawnerSupport != null) {
            getSpawnerAmount = spawnerSupport::getSpawnerAmount;
        } else {
            getSpawnerAmount = null;
        }

        spawners.clear();

        for (World world : worlds) {
            for (int X = minChunkX; X <= maxChunkX; X++) {
                for (int Z = minChunkZ; Z <= maxChunkZ; Z++) {
                    final Chunk chunk = world.getChunkAt(X, Z);
                    for (BlockState state : chunk.getTileEntities()) {
                        if (!isInIsland(state.getLocation())) continue;
                        if (!(state instanceof CreatureSpawner)) continue;

                        final CreatureSpawner spawner = (CreatureSpawner) state;
                        final Location location = spawner.getLocation();
                        final double x = location.getX();
                        final double z = location.getZ();
                        if (x < minX || x > maxX || z < minZ || z > maxZ) continue;

                        final EntityType type = spawner.getSpawnedType();
                        final String typeName = type.name();
                        if (!spawnerValueMap.containsKey(typeName)) continue;
                        final BigDecimal spawnerValue = BigDecimal.valueOf(spawnerValueMap.get(typeName));

                        final BigDecimal amount = (getSpawnerAmount == null) ? BigDecimal.ONE : BigDecimal.valueOf(getSpawnerAmount.apply(spawner));
                        spawners.compute(typeName, (name, original) -> {
                            if (original == null) return amount.intValue();
                            return original + amount.intValue();
                        });

                        value = value.add(spawnerValue.multiply(amount));
                    }
                }
            }
        }

        this.value = value.doubleValue();
        if (startvalue == -1) startvalue = value.doubleValue();

        for (Mission mission : IridiumSkyblock.getMissions().missions) {
            missionLevels.putIfAbsent(mission.name, 1);
            if (mission.levels.get(missionLevels.get(mission.name)).type == MissionType.VALUE_INCREASE) {
                setMission(mission.name, (int) (value.doubleValue() - startvalue - lastMissionValue));
            }
        }
        this.value += this.extravalue;
        if (IridiumSkyblock.getConfiguration().islandMoneyPerValue != 0)
            this.value += this.getMoney() / IridiumSkyblock.getConfiguration().islandMoneyPerValue;

        IslandWorthCalculatedEvent islandWorthCalculatedEvent = new IslandWorthCalculatedEvent(this, this.value);
        Bukkit.getPluginManager().callEvent(islandWorthCalculatedEvent);
        this.value = islandWorthCalculatedEvent.getIslandWorth();
        updateIslandData();
    }

    public void addWarp(Player player, Location location, String name, String password) {
        if (islandWarps.size() < IridiumSkyblock.getUpgrades().islandWarpUpgrade.getIslandUpgrade(getWarpLevel()).size) {
            islandWarps.add(new IslandWarp(location, name, password));
            player.sendMessage(Utils.color(IridiumSkyblock.getMessages().warpAdded.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        } else {
            player.sendMessage(Utils.color(IridiumSkyblock.getMessages().maxWarpsReached.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    public void addUser(User user) {
        if (members.size() < IridiumSkyblock.getUpgrades().islandMemberUpgrade.getIslandUpgrade(getMemberLevel()).size) {

            for (String player : members) {
                User u = User.getUser(player);
                Player p = Bukkit.getPlayer(u.name);
                if (p != null) {
                    p.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerJoinedYourIsland.replace("%player%", user.name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
            bans.remove(user.player);
            user.islandID = id;
            user.role = Role.Member;
            user.invites.clear();
            members.add(user.player);
            teleportHome(Bukkit.getPlayer(user.name));
        } else {
            Player player = Bukkit.getPlayer(user.name);
            if (player != null) {
                player.sendMessage(Utils.color(IridiumSkyblock.getMessages().maxMemberCount.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        membersGUI.getInventory().clear();
        membersGUI.addContent();
    }

    public void removeUser(User user) {
        IslandLeaveEvent islandLeaveEvent = new IslandLeaveEvent(this, user);
        Bukkit.getPluginManager().callEvent(islandLeaveEvent);
        if (islandLeaveEvent.isCancelled()) return;
        user.islandID = 0;
        Player player = Bukkit.getPlayer(user.name);
        if (player != null) {
            spawnPlayer(player);
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        members.remove(user.player);
        user.role = Role.Visitor;
        for (String member : members) {
            User u = User.getUser(member);
            Player p = Bukkit.getPlayer(u.name);
            if (p != null) {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().kickedMember.replace("%member%", user.name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        membersGUI.getInventory().clear();
        membersGUI.addContent();
    }

    public boolean isInIsland(Location location) {
        if (location == null) return false;
        return isInIsland(location.getX(), location.getZ());
    }

    public boolean isInIsland(double x, double z) {
        return x >= pos1.getX()
                && x <= pos2.getX()
                && z >= pos1.getZ()
                && z <= pos2.getZ();
    }

    public void init() {
        if (islandWarps == null) islandWarps = new ArrayList<>();
        this.playersOnIsland = new HashSet<>();
        this.lastPlayerCaching = 0L;
        if (netherschematic == null) {
            for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.getSchematics().schematicList) {
                if (fakeSchematic.overworldData.schematic.equals(schematic)) {
                    netherschematic = fakeSchematic.netherData.schematic;
                }
            }
        }
        if (biome == null) biome = IridiumSkyblock.getConfiguration().defaultBiome;
        if (valuableBlocks == null) valuableBlocks = new ConcurrentHashMap<>();
        if (spawners == null) spawners = new ConcurrentHashMap<>();
        if (stackedBlocks == null) stackedBlocks = new ConcurrentHashMap<>();
        if (members == null) {
            members = new HashSet<>();
            members.add(owner);
        }
        upgradeGUI = new UpgradeGUI(this);
        boosterGUI = new BoosterGUI(this);
        missionsGUI = new MissionsGUI(this);
        membersGUI = new MembersGUI(this);
        warpGUI = new WarpGUI(this);
        borderColorGUI = new BorderColorGUI(this);
        schematicSelectGUI = new SchematicSelectGUI(this);
        permissionsGUI = new PermissionsGUI(this);
        islandMenuGUI = new IslandMenuGUI(this);
        islandAdminGUI = new IslandAdminGUI(this);
        coopGUI = new CoopGUI(this);
        bankGUI = new BankGUI(this);
        biomeGUI = new MultiplePagesGUI<>(() -> {
            List<XBiome> biomes = IridiumSkyblock.getConfiguration().islandBiomes.keySet().stream().filter(xBiome -> xBiome.getEnvironment() == World.Environment.NORMAL).collect(Collectors.toList());
            int size = (int) (Math.floor(biomes.size() / ((double) IridiumSkyblock.getInventories().biomeGUISize - 9)) + 1);
            for (int i = 1; i <= size; i++) {
                biomeGUI.addPage(i, new BiomeGUI(this, i, World.Environment.NORMAL));
            }
        }, false);
        netherBiomeGUI = new MultiplePagesGUI<>(() -> {
            List<XBiome> biomes = IridiumSkyblock.getConfiguration().islandBiomes.keySet().stream().filter(xBiome -> xBiome.getEnvironment() == World.Environment.NETHER).collect(Collectors.toList());
            int size = (int) (Math.floor(biomes.size() / ((double) IridiumSkyblock.getInventories().biomeGUISize - 9)) + 1);
            for (int i = 1; i <= size; i++) {
                netherBiomeGUI.addPage(i, new BiomeGUI(this, i, World.Environment.NETHER));
            }
        }, false);
        visitorGUI = new VisitorGUI(this);

        if (upgradeLevels == null) upgradeLevels = new HashMap<>();
        if (boosterTimes == null) boosterTimes = new HashMap<>();
        if (bankItems == null) bankItems = new HashMap<>();

        failedGenerators = new HashSet<>();
        coopInvites = new HashSet<>();
        boosterId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), () -> {
            for (String booster : new ArrayList<>(boosterTimes.keySet())) {
                int time = boosterTimes.get(booster);
                if (time == 1) {
                    boosterTimes.remove(booster);
                    if (booster.equals(IridiumSkyblock.getBoosters().islandFlightBooster.name)) {
                        for (String player : members) {
                            Player p = Bukkit.getPlayer(player);
                            if (p != null) {
                                if ((!p.hasPermission("IridiumSkyblock.Fly") && !p.hasPermission("iridiumskyblock.fly")) && p.getGameMode().equals(GameMode.SURVIVAL)) {
                                    p.setAllowFlight(false);
                                    p.setFlying(false);
                                    User.getUser(p).flying = false;
                                }
                            }
                        }
                    }
                } else {
                    boosterTimes.put(booster, time - 1);
                }
            }
        }, 0, 20);
        if (permissions == null) {
            permissions = new HashMap<Role, Permissions>() {{
                for (Role role : Role.values()) {
                    put(role, new Permissions());
                }
            }};
        }
        Bukkit.getScheduler().runTaskLater(IridiumSkyblock.getInstance(), (Runnable) this::sendBorder, 20);
        Bukkit.getScheduler().runTaskLater(IridiumSkyblock.getInstance(), (Runnable) this::sendHolograms, 20);
    }

    public long canGenerate() {
        if (lastRegen == null) return 0;
        if (new Date().after(lastRegen)) return 0;
        return lastRegen.getTime() - System.currentTimeMillis();
    }

    public void teleportPlayersHome() {
        for (Player p : getPlayersOnIsland()) {
            teleportHome(p);
        }
    }

    public void pasteSchematic(boolean deleteBlocks) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, IridiumSkyblock.getConfiguration().regenCooldown);
        lastRegen = c.getTime();
        if (deleteBlocks) deleteBlocks();
        pasteSchematic();
        killEntities();
        //Reset island home
        for (Schematics.FakeSchematic schematic : IridiumSkyblock.getSchematics().schematicList) {
            if (!schematic.overworldData.schematic.equals(this.schematic) && !schematic.netherData.schematic.equals(this.netherschematic))
                continue;
            home = new Location(IslandManager.getWorld(), center.getX() + schematic.x, schematic.y, center.getZ() + schematic.z);
            this.setBiome(schematic.overworldData.biome);
            this.setNetherBiome(schematic.netherData.biome);
        }
    }

    public void pasteSchematic(Player player, boolean deleteBlocks) {
        pasteSchematic(deleteBlocks);
        User.getUser(player).teleportingHome = false;
        teleportHome(player);
        sendBorder(player);
        IridiumSkyblock.getNms().sendTitle(player, IridiumSkyblock.getMessages().islandCreated, 20, 40, 20);
        if (!IridiumSkyblock.getMessages().islandCreatedSubtitle.isEmpty())
            IridiumSkyblock.getNms().sendSubTitle(player, IridiumSkyblock.getMessages().islandCreatedSubtitle, 20, 40, 20);
    }

    private void pasteSchematic() {
        Bukkit.getPluginManager().callEvent(new IslandRegenEvent(this));
        stackedBlocks.clear();
        for (Schematics.FakeSchematic fakeSchematic : IridiumSkyblock.getSchematics().schematicList) {
            if (!fakeSchematic.overworldData.schematic.equals(this.schematic) && !fakeSchematic.netherData.schematic.equals(this.netherschematic))
                continue;
            IridiumSkyblock.getWorldEdit().paste(new File(IridiumSkyblock.getSchematicFolder(), schematic), center.clone().add(fakeSchematic.overworldData.xOffset, fakeSchematic.overworldData.yOffset, fakeSchematic.overworldData.zOffset), this);
            Location center = this.center.clone();
            if (IridiumSkyblock.getConfiguration().netherIslands) {
                center.setWorld(IslandManager.getNetherWorld());
                IridiumSkyblock.getWorldEdit().paste(new File(IridiumSkyblock.getSchematicFolder(), netherschematic), center.clone().add(fakeSchematic.netherData.xOffset, fakeSchematic.netherData.yOffset, fakeSchematic.netherData.zOffset), this);
            }
        }
    }

    public void clearInventories() {
        if (IridiumSkyblock.getConfiguration().clearInventories) {
            for (String player : members) {
                User user = User.getUser(player);
                Player p = Bukkit.getPlayer(user.name);
                if (p != null) {
                    p.getInventory().clear();
                    if (IridiumSkyblock.getConfiguration().clearEnderChests) p.getEnderChest().clear();
                }
            }
        }
    }

    public void sendHolograms(Player player) {
        World world = player.getWorld();
        User user = User.getUser(player);
        for (Object object : user.getHolograms()) {
            IridiumSkyblock.getNms().removeHologram(player, object);
        }
        user.clearHolograms();
        for (Location location : stackedBlocks.keySet()) {
            if (location.getWorld() != world) continue;
            Block block = location.getBlock();
            int amount = stackedBlocks.get(location);
            IridiumSkyblock.getNms().sendHologram(player, block.getLocation().add(0.5, -0.5, 0.5), Utils.processMultiplePlaceholders(IridiumSkyblock.getMessages().stackedBlocksHologram, Arrays.asList(new Utils.Placeholder("amount", amount + ""), new Utils.Placeholder("block", XMaterial.matchXMaterial(block.getType()).toString()))));
        }
    }

    public void sendHolograms() {
        for (Player player : getPlayersOnIsland()) {
            sendHolograms(player);
        }
    }

    public void teleportHome(Player p) {
        if (home == null) home = center;
        if (User.getUser(p).teleportingHome) {
            return;
        }
        if (isBanned(User.getUser(p)) && !members.contains(p.getUniqueId().toString()) && !p.hasPermission("iridiumskyblock.visitbypass")) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().bannedFromIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            return;
        }
        if (schematic == null) {
            User u = User.getUser(p);
            if (u.getIsland().equals(this)) {
                if (IridiumSkyblock.getSchematics().schematicList.size() == 1) {
                    for (Schematics.FakeSchematic schematic : IridiumSkyblock.getSchematics().schematicList) {
                        this.schematic = schematic.overworldData.schematic;
                        this.netherschematic = schematic.netherData.schematic;
                        break;
                    }
                } else {
                    p.openInventory(schematicSelectGUI.getInventory());
                }
            }
            return;
        }
        p.setFallDistance(0);
        if (members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().teleportingHome.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
        if (Utils.isSafe(home, this)) {
            p.teleport(home);
            sendBorder(p);
        } else {
            Location loc = Utils.getNewHome(this, this.home);
            if (loc != null) {
                this.home = loc;
                p.teleport(this.home);
                sendBorder(p);
            } else {
                User.getUser(p).teleportingHome = true;
                pasteSchematic(p, true);
            }
        }
    }

    public void teleportNetherHome(Player p) {
        Location netherHome = getNetherHome();

        if (User.getUser(p).teleportingHome) {
            return;
        }
        if (isBanned(User.getUser(p)) && !members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().bannedFromIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            return;
        }
        if (schematic == null) {
            User u = User.getUser(p);
            if (u.getIsland().equals(this)) {
                if (IridiumSkyblock.getSchematics().schematicList.size() == 1) {
                    for (Schematics.FakeSchematic schematic : IridiumSkyblock.getSchematics().schematicList) {
                        this.schematic = schematic.overworldData.schematic;
                        this.netherschematic = schematic.netherData.schematic;
                        break;
                    }
                } else {
                    p.openInventory(schematicSelectGUI.getInventory());
                }
            }
            return;
        }
        p.setFallDistance(0);
        if (members.contains(p.getUniqueId().toString())) {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().teleportingHome.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitingIsland.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            for (String pl : members) {
                Player player = Bukkit.getPlayer(User.getUser(pl).name);
                if (player != null) {
                    player.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitedYourIsland.replace("%player%", p.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
        }
        if (Utils.isSafe(getNetherHome(), this)) {
            p.teleport(getNetherHome());
            sendBorder(p);
        } else {

            Location loc = Utils.getNewHome(this, netherHome);
            if (loc != null) {
                p.teleport(netherHome);
                sendBorder(p);
            } else {
                User.getUser(p).teleportingHome = true;
                pasteSchematic(p, false);
            }
        }
    }

    public void updateIslandData() {
        IslandDataManager.cache.put(id, new IslandDataManager.IslandData(value, votes.size(), !visit));
    }

    public void delete() {
        Bukkit.getPluginManager().callEvent(new IslandDeleteEvent(this));

        Bukkit.getScheduler().cancelTask(membersGUI.scheduler);
        Bukkit.getScheduler().cancelTask(boosterGUI.scheduler);
        Bukkit.getScheduler().cancelTask(missionsGUI.scheduler);
        Bukkit.getScheduler().cancelTask(upgradeGUI.scheduler);
        Bukkit.getScheduler().cancelTask(warpGUI.scheduler);
        Bukkit.getScheduler().cancelTask(permissionsGUI.scheduler);
        Bukkit.getScheduler().cancelTask(islandMenuGUI.scheduler);
        Bukkit.getScheduler().cancelTask(islandAdminGUI.scheduler);
        Bukkit.getScheduler().cancelTask(coopGUI.scheduler);
        Bukkit.getScheduler().cancelTask(bankGUI.scheduler);
        if (generateID != -1) Bukkit.getScheduler().cancelTask(generateID);
        permissions.clear();
        spawnPlayers();
        for (String player : members) {
            User.getUser(player).islandID = 0;
            Player p = Bukkit.getPlayer(User.getUser(player).name);
            if (p != null) {
                p.closeInventory();
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().islandDeleted.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        killEntities();
        deleteBlocks();
        for (int id : coop) {
            IslandManager.getIslandViaId(id).coop.remove(id);
        }
        coop = null;
        hideBorder();
        this.owner = null;
        this.pos1 = null;
        this.pos2 = null;
        this.members = null;
        this.center = null;
        this.home = null;
        IslandManager.removeIsland(this);
        IridiumSkyblock.getInstance().saveConfigs();
        Bukkit.getScheduler().cancelTask(boosterId);
        boosterId = -1;
    }

    public void removeBan(User user) {
        if (bans == null) bans = new HashSet<>();
        bans.remove(user.player);
    }

    public void addBan(User user) {
        if (bans == null) bans = new HashSet<>();
        bans.add(user.player);
    }

    public void removeVote(User user) {
        if (votes == null) votes = new HashSet<>();
        votes.remove(user.player);
        updateIslandData();
    }

    public void addVote(User user) {
        if (votes == null) votes = new HashSet<>();
        votes.add(user.player);
        updateIslandData();
    }

    public boolean hasVoted(User user) {
        if (votes == null) votes = new HashSet<>();
        return votes.contains(user.player);
    }

    public int getVotes() {
        if (votes == null) votes = new HashSet<>();
        return votes.size();
    }

    public boolean isBanned(User user) {
        if (bans == null) bans = new HashSet<>();
        return bans.contains(user.player) && !user.bypassing;
    }

    public void addCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        for (String member : island.members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopGiven.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        for (String member : members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopAdded.replace("%player%", User.getUser(island.owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        coop.add(island.id);
        if (island.coop == null) island.coop = new HashSet<>();
        island.coop.add(id);
    }

    public void inviteCoop(Island island) {
        if (coopInvites == null) coopInvites = new HashSet<>();
        coopInvites.add(island.id);
        for (String member : members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                BaseComponent[] components = TextComponent.fromLegacyText(Utils.color(IridiumSkyblock.getMessages().coopInvite.replace("%player%", User.getUser(island.owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));

                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is coop " + User.getUser(island.owner).name);
                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(IridiumSkyblock.getMessages().coopHoverMessage).create());
                for (BaseComponent component : components) {
                    component.setClickEvent(clickEvent);
                    component.setHoverEvent(hoverEvent);
                }
                pl.getPlayer().spigot().sendMessage(components);
            }
        }
    }

    public void removeCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        coop.remove(island.id);
        if (island.coop == null) island.coop = new HashSet<>();
        island.coop.remove(id);
        for (String member : island.members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopTaken.replace("%player%", User.getUser(owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        for (String member : members) {
            Player pl = Bukkit.getPlayer(User.getUser(member).name);
            if (pl != null) {
                pl.sendMessage(Utils.color(IridiumSkyblock.getMessages().coopTaken.replace("%player%", User.getUser(island.owner).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        }
        coopGUI.getInventory().clear();
        coopGUI.addContent();
        island.coopGUI.getInventory().clear();
        island.coopGUI.addContent();
    }

    public void removeCoop(int id) {
        if (coop == null) coop = new HashSet<>();
        coop.remove(id);
    }

    public boolean isCoop(Island island) {
        if (coop == null) coop = new HashSet<>();
        if (island == null) return false;
        return coop.contains(island.id);
    }

    public Set<Integer> getCoop() {
        if (coop == null) coop = new HashSet<>();
        return coop;
    }

    public void spawnPlayers() {
        for (Player p : getPlayersOnIsland()) {
            spawnPlayer(p);
        }
    }

    public Set<Player> getPlayersOnIsland() {
        if (System.currentTimeMillis() >= lastPlayerCaching + (IridiumSkyblock.getConfiguration().playersOnIslandRefreshTime * 1000L)) {
            reloadPlayersOnIsland();
        }
        return playersOnIsland;
    }

    public void reloadPlayersOnIsland() {
        lastPlayerCaching = System.currentTimeMillis();
        playersOnIsland.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isInIsland(p.getLocation())) {
                playersOnIsland.add(p);
            }
        }
    }

    public void spawnPlayer(Player player) {
        if (player == null) return;
        if (Bukkit.getPluginManager().isPluginEnabled("EssentialsSpawn")) {
            EssentialsSpawn essentialsSpawn = (EssentialsSpawn) Bukkit.getPluginManager().getPlugin("EssentialsSpawn");
            Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
            player.teleport(essentialsSpawn.getSpawn(essentials.getUser(player).getGroup()));
        } else {
            World world = Bukkit.getWorld(IridiumSkyblock.getConfiguration().worldSpawn);
            if (world == null) world = Bukkit.getWorlds().get(0);
            player.teleport(world.getSpawnLocation());
        }
        playersOnIsland.remove(player);
    }

    public void setBiome(XBiome biome) {
        this.biome = biome;
        final World world = IslandManager.getWorld();
        Location pos1 = this.pos1.clone();
        Location pos2 = this.pos2.clone();
        pos1.setWorld(world);
        pos2.setWorld(world);
        final List<Chunk> chunks = new ArrayList<Chunk>() {{
            for (int X = pos1.getChunk().getX(); X <= pos2.getChunk().getX(); X++) {
                for (int Z = pos1.getChunk().getZ(); Z <= pos2.getChunk().getZ(); Z++) {
                    add(world.getChunkAt(X, Z));
                }
            }
        }};
        biome.setBiome(pos1, pos2).thenRunAsync(() -> {
            for (Chunk c : chunks) {
                for (Player p : world.getPlayers()) {
                    if (p.getLocation().getWorld() == world) {
                        IridiumSkyblock.getNms().sendChunk(p, c);
                    }
                }
            }
        });
    }

    public void setNetherBiome(XBiome biome) {
        if (!IridiumSkyblock.getConfiguration().netherIslands) return;
        this.netherBiome = biome;
        final World world = IslandManager.getNetherWorld();
        Location pos1 = this.pos1.clone();
        Location pos2 = this.pos2.clone();
        pos1.setWorld(world);
        pos2.setWorld(world);
        biome.setBiome(pos1, pos2).thenRunAsync(() -> {
            for (int X = pos1.getChunk().getX(); X <= pos2.getChunk().getX(); X++) {
                for (int Z = pos1.getChunk().getZ(); Z <= pos2.getChunk().getZ(); Z++) {
                    for (Player p : world.getPlayers()) {
                        if (p.getLocation().getWorld() == world) {
                            IridiumSkyblock.getNms().sendChunk(p, world.getChunkAt(X, Z));
                        }
                    }
                }
            }
        });
    }

    public void deleteBlocks() {
        clearInventories();
        valuableBlocks.clear();
        calculateIslandValue();
        final World world = IslandManager.getWorld();
        final World nether = IslandManager.getNetherWorld();
        setBlock(world);
        if (IridiumSkyblock.getConfiguration().netherIslands) {
            setBlock(nether);
        }
    }

    private void setBlock(World world) {
      SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
      generator.setScale(0.005D);
      final Config config = IridiumSkyblock.getConfiguration();

      for (int x = pos1.getBlockX(); x <= pos2.getBlockX(); x++) {
          for (int z = pos1.getBlockZ(); z <= pos2.getBlockZ(); z++) {
              int cx = x >> 4;
              int cz = z >> 4;
              int currentFloorHeight = (int) ((generator.noise(cx * 16 + x, cz * 16 + z, 0.5D, 0.5D, true) + 1) * (config.maxOceanFloorLevel - config.minOceanFloorLevel) + config.minOceanFloorLevel);
              setBlock(world, x, 0, z, Objects.requireNonNull(XMaterial.BEDROCK));
              for (int y = 1; y < currentFloorHeight; y++) {
                  setBlock(world, x, y, z, Objects.requireNonNull(XMaterial.GRAVEL));
              }
              setBlock(world, x, currentFloorHeight, z, Objects.requireNonNull(XMaterial.SAND));
              for (int y = currentFloorHeight + 1; y <= config.waterHeight; y++) {
                  XMaterial material = config.netherLavaOcean && world.getEnvironment() == Environment.NETHER ? XMaterial.LAVA : XMaterial.WATER;
                  setBlock(world, x, y, z, Objects.requireNonNull(material));
              }
              for (int y = config.waterHeight + 1; y <= 255; y++) {
                  XMaterial material = XMaterial.AIR;
                  setBlock(world, x, y, z, Objects.requireNonNull(material));
              }
            }
        }
    }

    private void setBlock(World world, int x, int y, int z, XMaterial type) {
        IridiumSkyblock.getNms().setBlockFast(world.getBlockAt(x, y, z), type.getId(), (byte) 0);
    }

    public void killEntities() {
        for (Entity entity : IslandManager.getWorld().getNearbyEntities(center, IridiumSkyblock.getUpgrades().islandSizeUpgrade.getIslandUpgrade(getSizeLevel()).size / 2.00, 255, IridiumSkyblock.getUpgrades().islandSizeUpgrade.getIslandUpgrade(getSizeLevel()).size / 2.00)) {
            if (!entity.getType().equals(EntityType.PLAYER)) {
                entity.remove();
            }
        }
        if (IridiumSkyblock.getConfiguration().netherIslands) {
            Location netherCenter = center.clone();
            netherCenter.setWorld(IslandManager.getNetherWorld());

            for (Entity entity : IslandManager.getNetherWorld().getNearbyEntities(netherCenter, IridiumSkyblock.getUpgrades().islandSizeUpgrade.getIslandUpgrade(getSizeLevel()).size / 2.00, 255, IridiumSkyblock.getUpgrades().islandSizeUpgrade.getIslandUpgrade(getSizeLevel()).size / 2.00)) {
                if (!entity.getType().equals(EntityType.PLAYER)) {
                    entity.remove();
                }
            }
        }
    }

    public void addExtraValue(double amount) {
        this.extravalue += amount;
    }

    public void removeExtraValue(double amount) {
        this.extravalue -= amount;
    }


    public Location getNetherHome() {
        Location netherHome = center.clone();
        netherHome.setWorld(IslandManager.getNetherWorld());
        return netherHome;
    }

    public void setOwner(OfflinePlayer owner) {
        for (String player : members) {
            User user = User.getUser(player);
            Player p = Bukkit.getPlayer(user.name);
            if (p != null) {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().transferdOwnership.replace("%player%", owner.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));

            }
        }
        User.getUser(this.owner).role = Role.CoOwner;
        this.owner = owner.getUniqueId().toString();
        User.getUser(owner).role = Role.Owner;
    }

    public void removeWarp(IslandWarp islandWarp) {
        islandWarps.remove(islandWarp);
    }

    public String getName() {
        if (name == null) name = User.getUser(owner).name;
        return name;
    }

    public double getBankItem(String name) {
        return bankItems.getOrDefault(name, 0.00);
    }

    public void setBankItem(String name, double amount) {
        bankItems.put(name, amount);
    }

    public void addBoosterTime(String booster, int time) {
        boosterTimes.put(booster, getBoosterTime(booster) + time);
    }

    public int getBoosterTime(String booster) {
        return boosterTimes.getOrDefault(booster, 0);
    }

    public void setUpgradeLevel(Upgrades.Upgrade upgrade, int level) {
        if (upgrade.getIslandUpgrade(level) == null) return;
        upgradeLevels.put(upgrade.name, level);

        pos1 = center.clone().subtract(IridiumSkyblock.getUpgrades().islandSizeUpgrade.getIslandUpgrade(getSizeLevel()).size / 2.00, 0, IridiumSkyblock.getUpgrades().islandSizeUpgrade.getIslandUpgrade(getSizeLevel()).size / 2.00);
        pos2 = center.clone().add(IridiumSkyblock.getUpgrades().islandSizeUpgrade.getIslandUpgrade(getSizeLevel()).size / 2.00, 0, IridiumSkyblock.getUpgrades().islandSizeUpgrade.getIslandUpgrade(getSizeLevel()).size / 2.00);
        sendBorder();
        setBiome(biome);
    }

    public int getUpgradeLevel(String upgrade) {
        return upgradeLevels.getOrDefault(upgrade, 1);
    }

    public int getSizeLevel() {
        return getUpgradeLevel(IridiumSkyblock.getUpgrades().islandSizeUpgrade.name);
    }

    public int getBlockLimitLevel() {
        return getUpgradeLevel(IridiumSkyblock.getUpgrades().islandBlockLimitUpgrade.name);
    }

    public int getMemberLevel() {
        return getUpgradeLevel(IridiumSkyblock.getUpgrades().islandMemberUpgrade.name);
    }

    public int getWarpLevel() {
        return getUpgradeLevel(IridiumSkyblock.getUpgrades().islandWarpUpgrade.name);
    }

    public int getOreLevel() {
        return getUpgradeLevel(IridiumSkyblock.getUpgrades().islandOresUpgrade.name);
    }

    public void setSizeLevel(int level) {
        setUpgradeLevel(IridiumSkyblock.getUpgrades().islandSizeUpgrade, level);
    }

    public void setBlockLimitLevel(int level) {
        setUpgradeLevel(IridiumSkyblock.getUpgrades().islandBlockLimitUpgrade, level);
    }

    public void setMemberLevel(int level) {
        setUpgradeLevel(IridiumSkyblock.getUpgrades().islandMemberUpgrade, level);
    }

    public void setWarpLevel(int level) {
        setUpgradeLevel(IridiumSkyblock.getUpgrades().islandWarpUpgrade, level);
    }

    public void setOreLevel(int level) {
        setUpgradeLevel(IridiumSkyblock.getUpgrades().islandOresUpgrade, level);
    }

    public Map<String, Integer> getMissionLevels() {
        if (missionLevels == null) missionLevels = new HashMap<>();
        return missionLevels;
    }

    public String getFormattedValue() {
        return Utils.NumberFormatter.format(value);
    }

    public String getFormattedLevel() {
        return Utils.NumberFormatter.format(value / IridiumSkyblock.getConfiguration().valuePerLevel);
    }

    public int getCrystals() {
        return (int) getBankItem("crystals");
    }

    public double getMoney() {
        return getBankItem("vault");
    }

    public int getExperience() {
        return (int) getBankItem("experience");
    }

    public void setCrystals(int amount) {
        setBankItem("crystals", amount);
    }

    public void setMoney(double amount) {
        setBankItem("vault", amount);
    }

    public void setExperience(int amount) {
        setBankItem("experience", amount);
    }

    public String getFormattedMoney() {
        return Utils.NumberFormatter.format(getMoney());
    }

    public String getFormattedExp() {
        return Utils.NumberFormatter.format(getExperience());
    }

    public String getFormattedCrystals() {
        return Utils.NumberFormatter.format(getCrystals());
    }

    public void save(Connection connection) {
        IslandManager.save(this, connection);
        IslandDataManager.save(this, connection);
    }
}

package me.bluevsred12.multigames.challenges;

import me.bluevsred12.multigames.Multigames;
import me.bluevsred12.multigames.UniqueItem;
import me.bluevsred12.multigames.utilities.Utilities;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParkourChallenge {
    private Multigames plugin;

    private BukkitScheduler scheduler;

    private World world;

    private Set<Player> competingPlayers;
    private Team redTeam;
    private Team blueTeam;

    private Location teamSelectionLocation;

    private static final Material PODIUM_VERIFICATION_TYPE = Material.BLACK_GLAZED_TERRACOTTA;

    private Set<ChallengeState> ongoingStates;
    private enum ChallengeState {
        TEAM_SELECTION,
        WAITING_PERIOD,
        PLAYING_PERIOD
    }

    private Map<ListenerType, Listener> listeners;
    private enum ListenerType {
        PRESSURE_PLATE,
        PLAYER_QUIT,
        PLAYER_MOVE,
        BLOCK_PLACE
    }

    public ParkourChallenge(Multigames plugin) {
        this.plugin = plugin;

        scheduler = Bukkit.getScheduler();

        world = plugin.getMainWorld();

        competingPlayers = plugin.getOnlinePlayers();
        redTeam = new Team(new Location(world, 61.0, 178, -316.0));
        blueTeam = new Team(new Location(world, 75.0, 178, -301.0));

        teamSelectionLocation = new Location(world, 68, 194, -308);

        ongoingStates = new HashSet<>();

        initializeListeners();

        startTeamSelectingPeriod();
    }

    private void initializeListeners() {
        listeners = new HashMap<>();

        listeners.put(ListenerType.PRESSURE_PLATE,
                new Listener() {
                    @EventHandler
                    public void onPlayerInteract(PlayerInteractEvent e) {
                        Player player = e.getPlayer();
                        Block clickedBlock = e.getClickedBlock();

                        // When the player steps on a trophy pressure plate during a match.
                        if (
                                isPlayingPeriod()
                                        && e.getAction().equals(Action.PHYSICAL)
                                        && clickedBlock != null
                                        && clickedBlock.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                            if (pickupTrophy(player, clickedBlock.getLocation().add(0, -1, 0).getBlock()))
                                e.setCancelled(true);
                        }
                    }
                });


        listeners.put(ListenerType.PLAYER_QUIT,
                new Listener() {
                    @EventHandler
                    public void onPlayerLeave(PlayerQuitEvent event) {
                        Player player = event.getPlayer();
                        Bukkit.broadcastMessage(player.getDisplayName() + " has been removed from the game!");

                        competingPlayers.remove(player);
                        redTeam.getMembers().remove(player);
                        blueTeam.getMembers().remove(player);
                    }
                });

        listeners.put(ListenerType.PLAYER_MOVE,
                new Listener() {
                    private final PotionEffect speedEffect = new PotionEffect(
                            PotionEffectType.SPEED,
                            10,
                            4,
                            false,
                            false,
                            false
                    );

                    private final int LAUNCH_COOLDOWN = 8;

                    private final Vector upwardLaunch = new Vector(0, 3, 0);
                    private final Vector blueForward = new Vector(-1, 0, -1);
                    private final Vector redForward = new Vector(1, 0, 1);

                    private Set<Player> launchedPlayers = new HashSet<>();

                    @EventHandler
                    public void onPlayerMove(PlayerMoveEvent event) {
                        Player player = event.getPlayer();
                        Material materialStoodOn = player.getLocation().add(0, -1, 0).getBlock().getType();
                        Set<Player> redMembers = redTeam.getMembers();
                        Set<Player> blueMembers = blueTeam.getMembers();
                        if (isTeamSelectingPeriod()) {
                            if (!redMembers.contains(player) && materialStoodOn.equals(Material.RED_WOOL)) {
                                blueMembers.remove(player);
                                redMembers.add(player);
                                player.sendMessage("Joined red team!");
                            }
                            if (!blueMembers.contains(player) && materialStoodOn.equals(Material.BLUE_WOOL)) {
                                redMembers.remove(player);
                                blueMembers.add(player);
                                player.sendMessage("Joined blue team!");
                            }
                        }
                        if (isPlayingPeriod()) {
                            Material materialBelowGround = player.getLocation().add(0, -2, 0).getBlock().getType();
                            if (materialStoodOn.equals(Material.WHITE_CONCRETE)) {
                                player.addPotionEffect(speedEffect);
                            }
                            if (materialBelowGround.equals(Material.BLUE_GLAZED_TERRACOTTA) && !launchedPlayers.contains(player)) {
                                launchedPlayers.add(player);
                                player.setVelocity(upwardLaunch);
                                scheduler.runTaskLater(plugin, () -> {
                                    player.setVelocity(blueForward);
                                    launchedPlayers.remove(player);
                                }, LAUNCH_COOLDOWN);
                            }
                            if (materialBelowGround.equals(Material.RED_GLAZED_TERRACOTTA) && !launchedPlayers.contains(player)) {
                                launchedPlayers.add(player);
                                player.setVelocity(upwardLaunch);
                                scheduler.runTaskLater(plugin, () -> {
                                    player.setVelocity(redForward);
                                    launchedPlayers.remove(player);
                                }, LAUNCH_COOLDOWN);
                            }
                        }
                    }
                });

        listeners.put(ListenerType.BLOCK_PLACE,
                new Listener() {
                    @EventHandler
                    public void onBlockPlace(BlockPlaceEvent event) {
                        validateTrophyPlacement(event);
                    }
                });

        for (Listener listener : listeners.values()) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    // game states
    private void startTeamSelectingPeriod() {
        ongoingStates.add(ChallengeState.TEAM_SELECTION);

        for (Player player : competingPlayers) {
            player.teleport(teamSelectionLocation);
        }

        scheduler.runTaskLater(plugin, this::startWaitingPeriod, 100);
    }

    private void startWaitingPeriod() {
        ongoingStates.clear();
        ongoingStates.add(ChallengeState.WAITING_PERIOD);

        for (Player player : competingPlayers) {
            if (redTeam.getMembers().contains(player)) player.teleport(redTeam.getSpawn());
            else if (blueTeam.getMembers().contains(player)) player.teleport(blueTeam.getSpawn());
            else {
                competingPlayers.remove(player);
                Bukkit.broadcastMessage(player.getDisplayName() + " did not select a team and will now be spectating!");
            }
        }

        scheduler.runTaskLater(plugin, this::startPlayingPeriod, 100);
    }

    private void startPlayingPeriod() {
        ongoingStates.clear();
        ongoingStates.add(ChallengeState.PLAYING_PERIOD);

        for (Player player : competingPlayers) {
            player.teleport(player.getLocation().add(0, -3, 0));
        }
        Bukkit.broadcastMessage("The game has begun!");
    }

    // playing period methods
    private void validateTrophyPlacement(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block placedBlock = event.getBlockPlaced();
        Block blockAdjacent = event.getBlockAgainst();
        Block blockBelow = event.getBlockPlaced().getRelative(0, -3, 0);

        Trophy trophy = Trophy.getTrophy(placedBlock.getType());

        if (trophy == null) { // Was the block a trophy?
            event.setBuild(false);
        } else if (blockBelow.getType() != PODIUM_VERIFICATION_TYPE) { // Was this placed on top a podium?
            event.setBuild(false);
        } else if (trophy.getStairs() != blockAdjacent.getType()) { // Was this placed on top a podium? (Double checking)
            player.spawnParticle(Particle.BARRIER, placedBlock.getLocation().add(0.5, 0.5,0.5), 1);
            player.playSound(placedBlock.getLocation(), Sound.BLOCK_BAMBOO_FALL, 1f, 1f);
            event.setBuild(false);
        } else {
            if (!placeTrophy(player, trophy, placedBlock)) {
                event.setBuild(false);
                player.playSound(placedBlock.getLocation(), Sound.BLOCK_BAMBOO_FALL, 1f, 1f);
            }
        }
    }

    private boolean placeTrophy(Player player, Trophy trophy, Block placedBlock) {
        String playerName = player.getDisplayName();
        String trophyName = trophy.getDisplayFriendlyName();
        Team team = getPlayerTeam(player);
        Team oppositeTeam = getOppositeTeam(team);
        if (team == null) {
            Bukkit.broadcastMessage("If you can see this message, I'm bad at programming!");
            return false;
        }

        TrophyTracker trophyTracker = team.getTrophyTracker();
        TrophyTracker opposingTrophyTracker = getOppositeTeam(team).getTrophyTracker();

        boolean isAlreadyPlaced = trophyTracker.hasTrophy(trophy);
        boolean isOppositeTrophyPlaced = opposingTrophyTracker.hasTrophy(trophy);
        if (isAlreadyPlaced && isOppositeTrophyPlaced) { // Shatter the enemy trophy if you and them both have one.
            Location opposingTrophyLocation = oppositeTeam.getTrophyTracker().getTrophyLocation(trophy);
            Location trophyLocation = placedBlock.getLocation();
            new BukkitRunnable() {
                @Override
                public void run() {
                    world.getBlockAt(opposingTrophyLocation).setType(Material.AIR);
                    world.getBlockAt(trophyLocation).setType(Material.AIR);
                    Bukkit.broadcastMessage(playerName + " has shattered the " + getTeamName(oppositeTeam) + " team's " + trophyName + " trophy!");
                    Utilities.spawnParticleLine(world, Particle.FLAME, 10, trophyLocation, opposingTrophyLocation);
                    world.spawnParticle(Particle.BLOCK_CRACK, trophyLocation, 8, 0.2, 0.2, 0.2,
                            trophy.getWood().createBlockData());
                    world.spawnParticle(Particle.BLOCK_CRACK, opposingTrophyLocation, 8, 0.2, 0.2, 0.2,
                            trophy.getWood().createBlockData());
                    world.playSound(opposingTrophyLocation, Sound.BLOCK_GLASS_BREAK, 50f, 0f);
                    opposingTrophyTracker.removeTrophy(trophy);
                }
            }.runTaskLater(plugin, 5);
            return true;
        } else if (!isAlreadyPlaced) { // Place the trophy onto the team podium
            new BukkitRunnable() {
                private int step = 0;
                private Block currentBlock = placedBlock;
                private Location startLocation = placedBlock.getLocation().add(0.5, 0.5, 0.5);
                private Location targetLocation = startLocation.clone().add(0, 3, 0);

                @Override
                public void run() {
                    if (step != 0) {
                        currentBlock.getRelative(BlockFace.UP).setType(currentBlock.getType());
                        currentBlock.setType(Material.AIR);
                        currentBlock = currentBlock.getRelative(BlockFace.UP);
                        Utilities.spawnParticleLine(world, Particle.CLOUD, 5, startLocation, targetLocation);
                    }
                    if (step == 0) {
                        world.playSound(
                                player.getLocation(),
                                Sound.BLOCK_NOTE_BLOCK_PLING,
                                SoundCategory.BLOCKS,
                                1f,
                                1f
                        );
                    } else if (step == 1 || step == 2) {
                        world.playSound(
                                currentBlock.getLocation(),
                                Sound.ITEM_FLINTANDSTEEL_USE,
                                1f,
                                1f
                        );
                    } else if (step >= 3) {
                        world.playSound(
                                currentBlock.getLocation(),
                                Sound.ENTITY_BLAZE_HURT,
                                1f,
                                0f
                        );
                        Bukkit.broadcastMessage(playerName + " has placed the " + trophyName + " trophy on " + getTeamName(team) + " team's podium!");
                        trophyTracker.addTrophy(trophy, currentBlock.getLocation());
                        this.cancel();
                    }
                    step++;
                }
            }.runTaskTimer(plugin, 0, 20);
            return true;
        } else {
            player.sendMessage("You must wait until you lose your existing trophy, or when the enemy gains one!");
            return false;
        }
    }

    private boolean pickupTrophy(Player player, Block woodBlock) {
        Trophy trophy = Trophy.getTrophy(woodBlock.getType());
        Block pressurePlate = woodBlock.getRelative(0,  1, 0);
        if (trophy == null) return false;

        player.getInventory().addItem(trophy.getItemStack());
        Bukkit.broadcastMessage(player.getDisplayName() + " has collected the " + trophy.getDisplayFriendlyName() + " trophy!");

        pressurePlate.setType(Material.AIR);
        woodBlock.setType(Material.AIR);
        player.playSound(woodBlock.getLocation(), Sound.ITEM_TRIDENT_HIT_GROUND, SoundCategory.BLOCKS, 1f, 0f);
        player.spawnParticle(
                Particle.CRIT,
                woodBlock.getLocation().add(0.5, 0.5, 0.5),
                10, 0.3, 0.3, 0.3, 0.2);

        scheduler.runTaskLater(plugin, () -> {
            woodBlock.setType(trophy.getWood());
            pressurePlate.setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        }, 60);
        return true;
    }

    // utility
    public boolean isTeamSelectingPeriod() {
        return ongoingStates.contains(ChallengeState.TEAM_SELECTION);
    }

    public boolean isPlayingPeriod() {
        return ongoingStates.contains(ChallengeState.PLAYING_PERIOD);
    }

    private String getTeamName(Team team) {
        if (team == redTeam) return "red";
        else return "blue";
    }

    private Team getOppositeTeam(Team team) {
        if (team == redTeam) return blueTeam;
        else return redTeam;
    }

    private Team getPlayerTeam(Player player) {
        if (redTeam.getMembers().contains(player)) return redTeam;
        if (blueTeam.getMembers().contains(player)) return blueTeam;
        return null;
    }

    public void cleanUp() {
        for (Listener listener : listeners.values()) {
            HandlerList.unregisterAll(listener);
        }
        redTeam.cleanUp(world);
        blueTeam.cleanUp(world);
    }
}

class Team {
    private Set<Player> members;

    private Location waitingSpawn;

    private TrophyTracker trophyTracker;

    Team(Location waitingSpawn) {
        members = new HashSet<>();
        this.waitingSpawn = waitingSpawn;
        trophyTracker = new TrophyTracker();
    }

    Set<Player> getMembers() {
        return members;
    }

    Location getSpawn() {
        return waitingSpawn.clone();
    }

    public TrophyTracker getTrophyTracker() {
        return trophyTracker;
    }

    public void cleanUp(World world) {
        trophyTracker.cleanUp(world);
    }
}

class TrophyTracker {
    private Map<Trophy, Location> trophyMap;

    TrophyTracker() {
        trophyMap = new HashMap<>();
    }

    public void addTrophy(Trophy trophy, Location location) {
        trophyMap.put(trophy, location);
    }

    public void removeTrophy(Trophy trophy) {
        trophyMap.remove(trophy);
    }

    public Location getTrophyLocation(Trophy trophy) {
        return trophyMap.get(trophy).clone().add(0.5, 0.5, 0.5);
    }

    public boolean hasTrophy(Trophy trophy) {
        return trophyMap.get(trophy) != null;
    }

    public void cleanUp(World world) {
        for (Location location : trophyMap.values()) {
            world.getBlockAt(location).setType(Material.AIR);
        }
    }
}

enum Trophy {
    OAK("Oak"),
    SPRUCE("Spruce"),
    BIRCH("Birch"),
    JUNGLE("Jungle"),
    ACACIA("Acacia"),
    DARK_OAK("Dark Oak")
    ;

    private final String nameID;
    private final String displayFriendlyName;
    private ItemStack itemStack;

    Trophy(String displayFriendlyName) {
        nameID = this.name();
        this.displayFriendlyName = displayFriendlyName;
        itemStack = UniqueItem.getItemStack(nameID + "_TROPHY");
    }

    public static Trophy getTrophy(Material searchedMaterial) {
        for (Trophy trophy : Trophy.values()) {
            if (trophy.getWood().equals(searchedMaterial)) return trophy;
        }
        return null;
    }

    public Material getWood() {
        return Material.getMaterial(nameID + "_WOOD");
    }

    public Material getStairs() {
        return Material.getMaterial(nameID + "_STAIRS");
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getDisplayFriendlyName() {
        return displayFriendlyName;
    }
}
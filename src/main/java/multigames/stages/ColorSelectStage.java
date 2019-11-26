package multigames.stages;

import multigames.Multigames;
import multigames.util.BackgroundRunnable;
import multigames.util.BackgroundRunnableManager;
import multigames.util.Cuboid;
import multigames.util.Rainbow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Set;

import static multigames.util.Note.*;

public class ColorSelectStage implements Stage {
    private Set<Player> playerSet;
    private BackgroundRunnableManager bgrm;
    private Multigames plugin;
    private World world;
    private Location beginLocation;

    public ColorSelectStage(Multigames plugin, Set<Player> players) {
        this.playerSet = players;
        bgrm = new BackgroundRunnableManager();
        this.plugin = plugin;
        world = plugin.getOverworld();
        beginLocation = new Location(world, -24, 151, 131);
    }

    public void debug() {
        bgrm.stop("select_phase_check");
    }

    /* BEGINNING PHASE */
    @Override
    public void beginPhase() {
        playerSet.forEach((p) -> p.sendMessage("Begin phase begins!"));
        playerSet.forEach((p) -> p.teleport(beginLocation));

        new BukkitRunnable() {
            @Override
            public void run() {
                selectionPhase();
            }
        }.runTaskLater(plugin, 20);
    }

    /* SELECTION PHASE */
    private void selectionPhase() {
        playerSet.forEach((p) -> p.sendMessage("Selection phase begins!"));

        ColorPlatform[] platforms = createPlatforms(world);

        // Update the colors of the platforms
        bgrm.addRunnable(new BackgroundRunnable("color_platform_update") {
            @Override
            public void stop() {
                for (ColorPlatform platform : platforms) {
                    platform.closePlatform();
                }
                cancel();
            }

            @Override
            public void run() {
                for (ColorPlatform platform : platforms) {
                    platform.update(playerSet);
                }
            }
        }).runTaskTimer(plugin, 0, 1);

        // Check if it's possible to progress to the next stage yet.
        bgrm.addRunnable(new BackgroundRunnable("select_phase_check") {
            @Override
            public void stop() {
                bgrm.stop("color_platform_update");
                cancel();
                entryPhase();
            }

            @Override
            public void run() {
                for (ColorPlatform platform : platforms) {
                    if (!platform.isReady())
                        return;
                }
                bgrm.stop("select_phase_check");
            }
        }).runTaskTimer(plugin, 0, 1);
    }

    private ColorPlatform[] createPlatforms(World world) {
        int[] coords = new int[] {
                -16, 152, 144,
                -21, 152, 146,
                -27, 152, 146,
                -32, 152, 144,
                -37, 152, 139,
                -39, 152, 134,
                -39, 152, 128,
                -37, 152, 123,
                -32, 152, 118,
                -27, 152, 116,
                -21, 152, 116,
                -16, 152, 118,
                -11, 152, 123,
                -9, 152, 128,
                -9, 152, 134,
                -11, 152, 139,
        };
        float[] notes = new float[] {
                C1, Cs1, D1, Ds1,
                E1, F1, Fs1, G1,
                Af1, A1, As1, B1,
                C2, Cs2, D2, Ds2
        };
        String[] colors = Rainbow.colors();
        ColorPlatform[] platforms = new ColorPlatform[Rainbow.length];
        for (int i = 0; i < Rainbow.length; i++) {
            platforms[i] = new ColorPlatform(
                    new Location(
                            world,
                            coords[i*3   ],
                            coords[i*3 +1],
                            coords[i*3 +2]
                    ),
                    colors[i],
                    notes[i]
            );
        }
        return platforms;
    }

    /* ENTRY PHASE */
    private void entryPhase() {
        playerSet.forEach((p) -> p.sendMessage("Entry phase begins!"));

        new BukkitRunnable() {
            @Override
            public void run() {
                endPhase();
            }
        }.runTaskLater(plugin, 20);
    }

    @Override
    public void endPhase() {
        playerSet.forEach((p) -> p.sendMessage("Ending phase begins!"));
        bgrm.stopAll();

        new BukkitRunnable() {
            @Override
            public void run() {
                playerSet.forEach((p) -> p.sendMessage("ColorSelectStage has been dereferenced from the competition manager!"));
                plugin.getCompetitionManager().dereferenceStage();
            }
        }.runTaskLater(plugin, 20);
    }

}

class ColorPlatform {
    private int count;
    private Cuboid materialRegion;
    private Cuboid playerRegion;
    private Material waitMaterial, readyMaterial, invalidMaterial;
    private float pitch;
    private Sound sound;

    public ColorPlatform(Location loc, String color, float pitch) {
        count = 3;
        materialRegion = new Cuboid(
                loc.clone().add(-1, -1, -1),
                loc.clone().add(1, -1, 1)
        );
        playerRegion = new Cuboid(
                loc.clone().add(-1, 4, -1),
                loc.clone().add(1, 0, 1)
        );
        waitMaterial = Material.getMaterial(color + "_STAINED_GLASS");
        readyMaterial = Material.getMaterial(color + "_CONCRETE");
        invalidMaterial = Material.REDSTONE_ORE;

        this.pitch = pitch;
        sound = Sound.BLOCK_NOTE_BLOCK_PLING;
    }

    public void update(Collection<Player> players) {
        int newCount = 0;
        for (Player p : players) {
            if (playerRegion.contains(p.getLocation()))
                newCount++;
        }
        if (newCount == count) return;
        count = newCount;
        updateAs(count, players);
    }

    public void updateAs(int playerCount, Collection<Player> players) {
        if (playerCount == 0) {
            materialRegion.fill(waitMaterial);
            return;
        }
        if (playerCount == 1) {
            materialRegion.fill(readyMaterial);
            players.forEach((p) -> p.playSound(p.getLocation(), sound, 1f, pitch));
            return;
        }
        if (playerCount >= 2)
            materialRegion.fill(invalidMaterial);
    }

    public void closePlatform() {
        materialRegion.fill(Material.COAL_BLOCK);
    }

    public boolean isReady() {
        return count == 1;
    }
}

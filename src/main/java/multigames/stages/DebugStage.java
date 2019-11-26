package multigames.stages;

import multigames.Multigames;
import multigames.util.BackgroundRunnable;
import multigames.util.BackgroundRunnableManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class DebugStage implements Stage {
    private Multigames plugin;
    private Set<Player> playerSet;
    private BackgroundRunnableManager bgrm;

    private Location spawnpoint;

    public DebugStage(Multigames plugin, Set<Player> playerSet) {
        this.plugin = plugin;
        this.playerSet = playerSet;
        bgrm = new BackgroundRunnableManager();

        spawnpoint = new Location(plugin.getOverworld(), 18, 60, 136);
    }

    @Override
    public void debug() {
        playerSet.forEach((p) -> p.sendMessage("The debug command has been executed! It actually does nothing oops"));
    }

    @Override
    public void beginPhase() {
        playerSet.forEach((p) -> p.teleport(spawnpoint));
        bgrm.addRunnable(new BackgroundRunnable("exit_check") {
            @Override
            public void stop() {
                endPhase();
            }

            @Override
            public void run() {
                if (!plugin.getOverworld().getBlockAt(13, 61, 117).isBlockPowered())
                    return;
                bgrm.stop("exit_check");
            }
        }).runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void endPhase() {
        playerSet.forEach((p) -> p.sendMessage("DEBUG! room has been exited!"));
        plugin.getCompetitionManager().dereferenceStage();
        bgrm.stopAll();
    }
}

package multigames;

import multigames.commands.ColorStageCommand;
import multigames.commands.DebugCommand;
import multigames.commands.DebugStageCommand;
import multigames.competition.CompetitionManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Multigames extends JavaPlugin {
    public CompetitionManager competitionManager;

    @Override
    public void onEnable() {
        competitionManager = new CompetitionManager();

        getCommand("startcolorstage").setExecutor(new ColorStageCommand(this));
        getCommand("startdebugstage").setExecutor(new DebugStageCommand(this));
        getCommand("compdebug").setExecutor(new DebugCommand(this));
        Bukkit.getConsoleSender().sendMessage("Multigames is active!");
    }

    public CompetitionManager getCompetitionManager() {
        return competitionManager;
    }

    public World getOverworld() {
        return getServer().getWorlds().get(0);
    }
}

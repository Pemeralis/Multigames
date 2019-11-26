package multigames.commands;

import multigames.Multigames;
import multigames.competition.StageAlreadyLoadedException;
import multigames.stages.ColorSelectStage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

public class ColorStageCommand implements CommandExecutor {
    private Multigames plugin;

    public ColorStageCommand(Multigames plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            plugin.getCompetitionManager().startStage(
                    new ColorSelectStage(plugin, new HashSet<>(Bukkit.getOnlinePlayers())
            ));
        } catch (StageAlreadyLoadedException e) {
            sender.sendMessage("ERROR: Unable to start the stage because another is already loaded!");
        }
        return true;
    }
}

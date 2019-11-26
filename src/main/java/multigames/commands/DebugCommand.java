package multigames.commands;

import multigames.Multigames;
import multigames.stages.Stage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DebugCommand implements CommandExecutor {
    private Multigames plugin;

    public DebugCommand(Multigames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Stage s = plugin.getCompetitionManager().getStage();
        if (s != null)
            s.debug();
        else
            sender.sendMessage("There is no ongoing stage!");
        return true;
    }
}

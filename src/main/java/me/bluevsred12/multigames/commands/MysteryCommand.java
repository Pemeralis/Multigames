package me.bluevsred12.multigames.commands;

import me.bluevsred12.multigames.Multigames;
import me.bluevsred12.multigames.utilities.Utilities;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MysteryCommand implements CommandExecutor {
    private Multigames plugin;

    public MysteryCommand(Multigames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Not enough arguments!");
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("No! This is not how you're supposed to play the game!");
            return true;
        }
        Player player = (Player) sender;
        Utilities.spawnParticleLine(
                plugin.getMainWorld(),
                Particle.CLOUD,
                20,
                player.getLocation(),
                player.getLocation().add(0, 10, 0)
        );
        player.sendMessage("Poof!");
        return true;
    }
}

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
            return true;
        }
        String argument = args[0];
        if (argument.equalsIgnoreCase("particles")) testParticles(sender);
        else if (argument.equalsIgnoreCase("titles")) testTitles(sender);
        else sender.sendMessage(argument + " is not a testable argument!");
        return true;
    }

    private void testTitles(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("No! This is not how you're supposed to play the game!");
            return;
        }
        Player player = (Player) sender;

        player.sendActionBar("ActionBar message sent!");
        player.sendTitle("Title message has been sent!", "Subtitle too!", 0, 40, 20);
    }

    private void testParticles(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("No! This is not how you're supposed to play the game!");
            return;
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
    }
}

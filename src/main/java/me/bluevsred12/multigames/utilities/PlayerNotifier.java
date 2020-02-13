package me.bluevsred12.multigames.utilities;

import org.bukkit.entity.Player;

import java.util.Set;

public class PlayerNotifier {
    private Set<Player> receivers;

    public PlayerNotifier(Set<Player> players) {
        receivers = players;
    }
}

package me.bluevsred12.multigames.utilities;

import org.bukkit.entity.Player;

import java.util.Set;

public class PlayerNotifier {
    private Set<Player> receivers;

    private String formattingPrefix;

    public PlayerNotifier(Set<Player> players) {
        receivers = players;
    }

    public void setFormattingPrefix(String formattingPrefix) {
        this.formattingPrefix = formattingPrefix + " ";
    }

    public void sendActionBar(String message) {
        receivers.forEach(player -> player.sendActionBar(Utilities.colorText(formattingPrefix + message)));
    }

    public void sendTitle(String title, String subtitle, int time) {
        receivers.forEach(player -> player.sendTitle(
                Utilities.colorText(formattingPrefix + title),
                Utilities.colorText(formattingPrefix + subtitle),
                0, time, time / 4)
        );
    }
}

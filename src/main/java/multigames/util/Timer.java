package multigames.util;

import multigames.Multigames;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class Timer {
    private Multigames plugin;

    private BossBar bossBar;

    private int current;
    private int end;

    public Timer(BossBar bossBar, int end, Collection<Player> players) {
        this.bossBar = bossBar;
        this.end = end;
        players.forEach(bossBar::addPlayer);
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {

            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void runWhile(BukkitRunnable runnable) {
        runWhile(runnable, 0, end);
    }

    public void runWhile(BukkitRunnable runnable, int start, int end) {

    }

    public void finishLastSeconds(BukkitRunnable runnable, int seconds) {

    }

    public void endWith(BukkitRunnable runnable) {

    }
}
package me.bluevsred12.multigames.utilities;

import me.bluevsred12.multigames.Multigames;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Set;

public class Timer {
    private Multigames plugin;

    private BukkitScheduler scheduler;

    private BossBar bossBar;
    private String title;
    private Sound tickingSound;
    private Sound finishingUpSound;
    private Sound completionSound;

    private Set<Player> players;

    private BukkitRunnable runnable;

    private static final int TICKS_PER_SECOND = 20;
    private final int TOTAL_TIME;
    private int secondsPassed;

    public Timer(Multigames plugin, String title, Set<Player> players, BukkitRunnable runnable, int timeToComplete) {
        this.plugin = plugin;
        scheduler = Bukkit.getScheduler();
        this.title = title;

        bossBar = Bukkit.createBossBar(
                title,
                BarColor.BLUE,
                BarStyle.SOLID);

        this.players = players;
        this.runnable = runnable;

        TOTAL_TIME = timeToComplete;
        secondsPassed = 0;
    }

    public void start() {
        for (Player player : players) {
            bossBar.addPlayer(player);
        }
    }

    private void incrementProgress() {
        secondsPassed++;
        bossBar.setProgress(TOTAL_TIME);
    }

    private void initializeBossBar() {
        bossBar.setProgress(TOTAL_TIME - secondsPassed);
        bossBar.setVisible(true);
    }

    public int getSecondsPassed() {
        return secondsPassed;
    }

    public int getRemainingSeconds() {
        return TOTAL_TIME - secondsPassed;
    }

}

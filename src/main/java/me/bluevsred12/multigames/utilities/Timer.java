package me.bluevsred12.multigames.utilities;

import me.bluevsred12.multigames.Multigames;
import org.bukkit.Bukkit;
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

    private Set<Player> players;

    private Runnable runnable;

    private static final int TICKS_PER_SECOND = 20;
    private final int TOTAL_TIME;
    private int secondsPassed;

    public Timer(Multigames plugin, String title, Set<Player> players, Runnable runnable, int timeToComplete) {
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
        initializeBossBar();

        for (Player player : players) {
            bossBar.addPlayer(player);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                incrementTimer();
                if (secondsPassed == TOTAL_TIME) {
                    runnable.run();
                    cleanUp();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void incrementTimer() {
        secondsPassed++;
        updateBossBarProgress();
    }

    private void initializeBossBar() {
        updateBossBarProgress();
        bossBar.setVisible(true);
    }

    private void updateBossBarProgress() {
        bossBar.setTitle(
                title + " : "
                + Utilities.convertToTimeFormat(TOTAL_TIME - secondsPassed));
        bossBar.setProgress(1 - (double) secondsPassed / TOTAL_TIME);
    }

    public int getSecondsPassed() {
        return secondsPassed;
    }

    public int getRemainingSeconds() {
        return TOTAL_TIME - secondsPassed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void cleanUp() {
        bossBar.removeAll();
    }
}

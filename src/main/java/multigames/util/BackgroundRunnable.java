package multigames.util;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class BackgroundRunnable extends BukkitRunnable {
    private String name;

    public BackgroundRunnable(String name) {
        super();
        this.name = name;
    }

    abstract public void stop();

    public String getName() {
        return name;
    }
}

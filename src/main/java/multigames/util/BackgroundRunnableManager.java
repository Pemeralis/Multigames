package multigames.util;

import java.util.ArrayList;
import java.util.List;

public class BackgroundRunnableManager {
    private List<BackgroundRunnable> bgRunnables;

    public BackgroundRunnableManager() {
        bgRunnables = new ArrayList<>();
    }

    public BackgroundRunnable addRunnable(BackgroundRunnable runnable) {
        bgRunnables.add(runnable);
        return runnable;
    }

    public void stop(String key) {
        for (BackgroundRunnable r : bgRunnables) {
            if (r.getName().equals(key)) {
                r.stop();
                bgRunnables.remove(r);
                return;
            }
        }
    }

    public void stopAll() {
        bgRunnables.forEach(BackgroundRunnable::stop);
        bgRunnables.clear();
    }
}

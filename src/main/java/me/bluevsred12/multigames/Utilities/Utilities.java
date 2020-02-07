package me.bluevsred12.multigames.Utilities;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Utilities {
    public static void spawnParticleLine(World world, Particle particle, int count, Location start, Location end) {
        start = start.clone();
        end = end.clone();
        Vector direction = end.subtract(start).toVector();
        direction.multiply(1.0 / count);
        for (int i = 0; i < count; i++) {
            world.spawnParticle(particle, start, 1, 0, 0, 0, 0);
            start.add(direction);
        }
    }
}

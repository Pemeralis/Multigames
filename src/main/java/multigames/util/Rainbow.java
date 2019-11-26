package multigames.util;

import org.bukkit.Material;

public class Rainbow {
    public static final int length = 16;
    private static final String[] rainbow = new String[] {
            "BROWN",
            "RED",
            "ORANGE",
            "YELLOW",

            "LIME",
            "GREEN",
            "CYAN",
            "LIGHT_BLUE",

            "BLUE",
            "PURPLE",
            "MAGENTA",
            "PINK",

            "WHITE",
            "LIGHT_GRAY",
            "GRAY",
            "BLACK",
    };

    public static String[] colors() {
        return rainbow.clone();
    }

    public static Material[] concrete() {
        return material("CONCRETE");
    }

    public static Material[] glass() {
        return material("_STAINED_GLASS");
    }

    private static Material[] material(String str) {
        Material[] materials = new Material[rainbow.length];
        for (int i = 0; i < rainbow.length; i++) {
            materials[i] = Material.getMaterial(rainbow[i] + "_" + str);
        }
        return materials;
    }
}

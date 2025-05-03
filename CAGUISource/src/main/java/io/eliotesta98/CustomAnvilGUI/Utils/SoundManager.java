package io.eliotesta98.CustomAnvilGUI.Utils;

import org.bukkit.entity.*;
import org.bukkit.*;

import java.lang.reflect.Method;

public class SoundManager {

    public static void playSound(Player player, Sound sound, float n, float n2) {
        player.playSound(player.getLocation(), sound, n, n2);
    }

    public static void playSound(Location location, Sound sound, float n, float n2) {
        location.getWorld().playSound(location, sound, n, n2);
    }

    public static Sound getSound(String sound) {
        if (sound.equalsIgnoreCase("")) {
            return null;
        }
        try {
            Method valueOfMethod = Sound.class.getMethod("valueOf", String.class);
            return (Sound) valueOfMethod.invoke(null, sound);
        } catch (Exception e) {
            return null;
        }
    }
}



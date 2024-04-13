package io.eliotesta98.AnvilPlus.Utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoundManager {
    public static void playSound(final CommandSender commandSender, final Sound sound, final float n, final float n2) {
        if (commandSender instanceof Player) {
            final Player player = (Player) commandSender;
            player.playSound(player.getLocation(), sound, n, n2);
        }
    }

    public static void playSound(final Player player, final Sound sound, final float n, final float n2) {
        player.playSound(player.getLocation(), sound, n, n2);
    }

    public static void playSound(final Location location, final Sound sound, final float n, final float n2) {
        location.getWorld().playSound(location, sound, n, n2);
    }
}

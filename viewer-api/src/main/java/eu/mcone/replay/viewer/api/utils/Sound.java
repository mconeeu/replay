package eu.mcone.replay.viewer.api.utils;

import eu.mcone.replay.viewer.api.event.PlaySoundEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class Sound {

    public static void playSound(final Player player, final org.bukkit.Sound sound, final float i, final float ii) {
        player.playSound(player.getLocation(), sound, i, ii);
        Bukkit.getPluginManager().callEvent(new PlaySoundEvent(player.getLocation(), sound, i, ii));
    }

    public static void playSound(final Location location, final org.bukkit.Sound sound, final float i, final float ii) {
        location.getWorld().playSound(location, sound, i, ii);
        Bukkit.getPluginManager().callEvent(new PlaySoundEvent(location, sound, i, ii));
    }
}

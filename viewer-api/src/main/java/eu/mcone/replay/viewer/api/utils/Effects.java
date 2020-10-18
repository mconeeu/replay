package eu.mcone.replay.viewer.api.utils;

import eu.mcone.gameapi.api.replay.event.PlayEffectEvent;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class Effects {

    public static void playSEffect(final Player player, final Effect effect, final int i) {
        player.playEffect(player.getLocation(), effect, 1);
        Bukkit.getPluginManager().callEvent(new PlayEffectEvent(effect, i, player.getLocation()));
    }

    public static void playSEffect(final Location location, final Effect effect, final int i) {
        location.getWorld().playEffect(location, effect, 1);
        Bukkit.getPluginManager().callEvent(new PlayEffectEvent(effect, i, location));
    }
}

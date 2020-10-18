package eu.mcone.replay.sever.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void on(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }
}

package eu.mcone.replay.sever.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MobSpawningListener implements Listener {

    @EventHandler
    public void on(CreatureSpawnEvent e) {
        e.setCancelled(true);
    }
}

package eu.mcone.replay.sever.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void on(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        e.setCancelled(true);
    }
}

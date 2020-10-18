package eu.mcone.replay.sever.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemListener implements Listener {

    @EventHandler
    public void on(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }
}

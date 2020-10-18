package eu.mcone.replay.sever.listener;

import eu.mcone.coresystem.api.bukkit.event.npc.NpcInteractEvent;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.replay.sever.inventory.ReplayNpcInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NpcInteractListener implements Listener {

    @EventHandler
    public void on(NpcInteractEvent e) {
        NPC npc = e.getNpc();
        Player player = e.getPlayer();

        if (npc.getData().getName().equalsIgnoreCase("replay")) {
            new ReplayNpcInventory(player);
        }
    }
}

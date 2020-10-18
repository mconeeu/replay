package eu.mcone.replay.sever.listener;

import eu.mcone.coresystem.api.bukkit.event.player.CorePlayerLoadedEvent;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.replay.sever.ReplayServer;
import eu.mcone.replay.sever.objectives.MainReplayObjective;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void on(CorePlayerLoadedEvent e) {
        CorePlayer cp = e.getPlayer();
        Player player = cp.bukkit();
        ReplayServer.getInstance().getViewers().add(player);
        cp.getScoreboard().setNewObjective(new MainReplayObjective());
        player.teleport(ReplayServer.getInstance().getReplayWorld().getLocation("spawn"));
        player.getInventory().clear();
        player.getInventory().setItem(InventorySlot.ROW_1_SLOT_5, PlayerInteractListener.SPAWN);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
    }
}

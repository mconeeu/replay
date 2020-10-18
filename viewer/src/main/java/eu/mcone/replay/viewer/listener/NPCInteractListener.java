package eu.mcone.replay.viewer.listener;

import eu.mcone.coresystem.api.bukkit.event.npc.NpcInteractEvent;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.viewer.ReplayViewer;
import eu.mcone.replay.viewer.container.ReplayContainer;
import eu.mcone.replay.viewer.inventory.ReplayPlayerInteractInventory;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;

public class NPCInteractListener implements Listener {

    private final ReplayContainer container;
    @Setter
    @Getter
    private boolean unregister = false;

    public NPCInteractListener(ReplayContainer container) {
        this.container = container;
        ReplayViewer.getInstance().registerEvents(this);
    }

    @EventHandler
    public void on(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        Player player = (Player) e.getWhoClicked();
        if (unregister) {
            e.getHandlers().unregister(this);
        } else {
            if (inv != null && !e.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
                for (ReplayPlayer replayPlayer : container.getReplay().getPlayers()) {
                    if (replayPlayer.getNpc().getData().getName().equalsIgnoreCase(inv.getName())) {
                        if (replayPlayer.getInventoryViewers().containsKey(player)) {
                            e.setCancelled(true);
                        }
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if (e.isSneaking()) {
            if (container.isInCamera(player)) {
                container.leaveCamera(player);
            }
        }
    }

    @EventHandler
    public void on(NpcInteractEvent e) {
        NPC npc = e.getNpc();

        if (unregister) {
            e.getHandlers().unregister(this);
        } else {
            Player player = e.getPlayer();
            if (npc instanceof PlayerNpc) {
                if (!container.isInCamera(player)) {
                    for (ReplayPlayer replayPlayer : container.getReplay().getPlayers()) {
                        if (replayPlayer.getNpc().equals(npc)) {
                            new ReplayPlayerInteractInventory(container, replayPlayer, player);
                            break;
                        }
                    }
                }
            }
        }
    }
}

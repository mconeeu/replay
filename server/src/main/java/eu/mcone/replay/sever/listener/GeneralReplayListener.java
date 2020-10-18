package eu.mcone.replay.sever.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;

import eu.mcone.replay.sever.objectives.MainReplayObjective;
import eu.mcone.replay.sever.objectives.ReplayObjective;
import eu.mcone.replay.viewer.api.ReplayViewer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.event.PlayerLeaveReplayCameraEvent;
import eu.mcone.replay.viewer.api.event.runner.ReplayJoinEvent;
import eu.mcone.replay.viewer.api.event.runner.ReplayQuitEvent;
import eu.mcone.replay.viewer.api.event.runner.ReplayStopEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import static eu.mcone.replay.sever.listener.PlayerInteractListener.setItem;

public class GeneralReplayListener implements Listener {

    @EventHandler
    public void on(ReplayJoinEvent e) {
        ReplayContainer container = e.getContainer();
        CoreWorld coreWorld = CoreSystem.getInstance().getWorldManager().getWorld(e.getContainer().getReplay().getWorld());
        if (coreWorld != null) {
            Player player = e.getPlayer();
            CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(player);
            corePlayer.getScoreboard().setNewObjective(new ReplayObjective(container.getReplay()));
            setItem(e.getContainer());
            player.teleport(coreWorld.getLocation("spawn"));

            for (Player all : e.getContainer().getViewers()) {
                ReplayViewer.getInstance().getMessenger().send(all, "§7Der Spieler §e" + e.getPlayer().getName() + " §7hat das Replay betreten!");
            }
        }
    }

    @EventHandler
    public void on(ReplayQuitEvent e) {
        Player player = e.getPlayer();
        player.getInventory().clear();
        CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(player);
        corePlayer.getScoreboard().setNewObjective(new ReplayObjective(e.getContainer().getReplay()));
        player.getInventory().setItem(InventorySlot.ROW_1_SLOT_5, PlayerInteractListener.SPAWN);

        // TODO: Add replay world
//        CoreWorld coreWorld = CoreSystem.getInstance().getWorldManager().getWorld(GamePlugin.getGamePlugin().getGameConfig().parseConfig().getLobby());
//        if (coreWorld != null) {
//            player.teleport(coreWorld.getLocation("spawn"));
//            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
//        }

        for (Player viewers : e.getContainer().getViewers()) {
            ReplayViewer.getInstance().getMessenger().send(viewers, "§7Der Spieler §e" + e.getPlayer().getName() + " §7hat das Replay verlassen!");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(ReplayStopEvent e) {
        ReplayContainer container = ReplayViewer.getInstance().getReplayViewManager().getContainer(e.getContainerID());
        if (container != null) {
            for (Player player : container.getViewers()) {
                CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(player);
                corePlayer.getScoreboard().setNewObjective(new MainReplayObjective());

                player.getInventory().clear();
                player.getInventory().setItem(InventorySlot.ROW_1_SLOT_5, PlayerInteractListener.SPAWN);
                ReplayViewer.getInstance().getMessenger().send(player, "§7Das Replay ist beendet, du wurdest zurück teleportiert!");
            }
        }
    }

    @EventHandler
    public void on(PlayerLeaveReplayCameraEvent e) {
        ReplayContainer container = e.getContainer();
        CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(e.getPlayer());
        corePlayer.getScoreboard().setNewObjective(new ReplayObjective(container.getReplay()));
        ReplayViewer.getInstance().getMessenger().send(corePlayer.bukkit(), "§7Du hast die Ansicht des Spielers §averlassen§7.");
    }
}

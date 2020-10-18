package eu.mcone.replay.viewer.container;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.replay.core.api.event.objective.ReplayScoreboardUpdateEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;

@Getter
public class ReplayContainerListener implements Listener {

    private final ReplayContainer replayContainer;

    public ReplayContainerListener(ReplayContainer replayContainer) {
        this.replayContainer = replayContainer;
    }

    @EventHandler
    public void on(ReplayScoreboardUpdateEvent e) {
        for (Player player : replayContainer.getInCamera().keySet()) {
            CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(player);
            corePlayer.getScoreboard().getObjective(DisplaySlot.SIDEBAR).reload();
        }
    }
}

package eu.mcone.replay.viewer.api.event;

import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class PlayerLeaveReplayCameraEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final ReplayContainer container;
    private final Player player;
    private final PlayerNpc playerNpc;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

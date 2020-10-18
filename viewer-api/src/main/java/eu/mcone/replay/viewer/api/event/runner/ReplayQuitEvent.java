package eu.mcone.replay.viewer.api.event.runner;

import eu.mcone.replay.viewer.api.container.ReplayContainer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class ReplayQuitEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final ReplayContainer container;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

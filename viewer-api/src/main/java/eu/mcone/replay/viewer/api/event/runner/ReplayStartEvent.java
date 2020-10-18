package eu.mcone.replay.viewer.api.event.runner;

import eu.mcone.replay.viewer.api.Replay;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class ReplayStartEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final ReplayContainer container;
    private final Replay replay;

    public HandlerList getHandlers() {
        return handlerList;
    }

}

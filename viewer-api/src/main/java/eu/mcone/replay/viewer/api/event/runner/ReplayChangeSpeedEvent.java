package eu.mcone.replay.viewer.api.event.runner;

import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.runner.ReplaySpeed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class ReplayChangeSpeedEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final String sessionID;
    private final ReplayContainer container;
    private final ReplaySpeed currentSpeed;
    private final ReplaySpeed newSpeed;
    @Setter
    private boolean cancelled;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

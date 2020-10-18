package eu.mcone.replay.viewer.api.event.container;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ReplayContainerRemovedEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final String replayID;
    private final UUID containerID;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

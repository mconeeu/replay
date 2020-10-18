package eu.mcone.replay.viewer.api.event.runner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class ReplayStopEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final String replayID;
    private final UUID containerID;
    private final int lastTick;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

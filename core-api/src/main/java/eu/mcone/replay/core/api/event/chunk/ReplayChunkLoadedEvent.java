package eu.mcone.replay.core.api.event.chunk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class ReplayChunkLoadedEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final String sessionID;
    private final String chunkID;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

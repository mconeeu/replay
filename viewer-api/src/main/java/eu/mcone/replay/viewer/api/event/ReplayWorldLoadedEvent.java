package eu.mcone.replay.viewer.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class ReplayWorldLoadedEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final String sessionID;
    private final String world;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

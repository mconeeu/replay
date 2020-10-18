package eu.mcone.replay.viewer.api.event.runner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ReplaySkipEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final String sessionID;
    private final UUID containerID;
    @Setter
    private boolean cancelled;

    public HandlerList getHandlers() {
        return handlerList;
    }
}

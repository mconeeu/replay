package eu.mcone.replay.core.api.event.objective;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class ReplayScoreboardUpdateEvent extends Event {
    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Map<Integer, String> scores;

    public HandlerList getHandlers() {
        return handlerList;
    }

}

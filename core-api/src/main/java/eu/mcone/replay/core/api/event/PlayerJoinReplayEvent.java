package eu.mcone.replay.core.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class PlayerJoinReplayEvent extends Event {

    @Getter
    private final Player player;

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    public HandlerList getHandlers() {
        return handlerList;
    }
}

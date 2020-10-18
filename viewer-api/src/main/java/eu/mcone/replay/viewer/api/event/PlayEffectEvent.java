package eu.mcone.replay.viewer.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class PlayEffectEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final Effect effect;
    private final int i;
    private final Location location;

    public HandlerList getHandlers() {
        return handlerList;
    }

}

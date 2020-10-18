package eu.mcone.replay.viewer.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter
public class PlaySoundEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    private final Location location;
    private final Sound sound;
    private final float i;
    private final float ii;

    public HandlerList getHandlers() {
        return handlerList;
    }

}

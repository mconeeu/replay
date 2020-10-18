package eu.mcone.replay.recorder.api;

import eu.mcone.replay.core.api.CoreReplay;
import org.bukkit.entity.Player;

public interface Replay extends CoreReplay {

    void record();

    void save();

    void addPlayer(final Player player);

    void removePlayer(final Player player);
}

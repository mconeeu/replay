package eu.mcone.replay.viewer.api.runner;

import eu.mcone.replay.viewer.api.container.ReplayContainer;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface ServerRunner {

    ReplayContainer getContainer();

    boolean isSync();

    boolean isForward();

    boolean isPlaying();

    ReplaySpeed getSpeed();

    Collection<Player> getViewers();
}

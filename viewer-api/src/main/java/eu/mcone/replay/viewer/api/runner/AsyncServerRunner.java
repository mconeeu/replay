package eu.mcone.replay.viewer.api.runner;

import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.utils.SkipUnit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public interface AsyncServerRunner {

    ReplayContainer getContainer();

    AtomicInteger getCurrentTick();

    boolean isSync();

    boolean isForward();

    void setForward(boolean forward);

    boolean isPlaying();

    void setPlaying(boolean playing);

    ReplaySpeed getSpeed();

    void setSpeed(ReplaySpeed speed);

    void play();

    void stop();

    void restart();

    void skip(SkipUnit unit, int ticks);

    Collection<Player> getViewers();
}

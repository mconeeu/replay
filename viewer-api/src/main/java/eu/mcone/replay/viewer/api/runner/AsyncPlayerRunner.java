package eu.mcone.replay.viewer.api.runner;

import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.utils.SkipUnit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public interface AsyncPlayerRunner {

    ReplayContainer getContainer();

    ReplayPlayer getPlayer();

    AtomicInteger getCurrentTick();

    boolean isSync();

    boolean isBreaking();

    void setBreaking(boolean breaking);

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

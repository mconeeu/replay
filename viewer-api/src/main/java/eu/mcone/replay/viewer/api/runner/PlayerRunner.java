package eu.mcone.replay.viewer.api.runner;

import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.utils.SkipUnit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public interface PlayerRunner {

    ReplayContainer getContainer();

    ReplayPlayer getPlayer();

    boolean isSync();

    boolean isBreaking();

    void setBreaking(boolean breaking);

    boolean isForward();

    boolean isPlaying();

    ReplaySpeed getSpeed();

    Collection<Player> getViewers();

    void play();

    void stop();

    void skip(SkipUnit unit, int amount);

    AtomicInteger getCurrentTick();
}

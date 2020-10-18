package eu.mcone.replay.viewer.api.container;

import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.replay.core.api.chunk.ReplayChunkHandler;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.viewer.api.Replay;
import eu.mcone.replay.viewer.api.runner.AsyncPlayerRunner;
import eu.mcone.replay.viewer.api.runner.ReplayRunner;
import eu.mcone.replay.viewer.api.runner.ReplaySpeed;
import eu.mcone.replay.viewer.api.utils.SkipUnit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public interface ReplayContainer {

    Replay getReplay();

    UUID getContainerUUID();

    int getTick();

    boolean isPlaying();

    boolean isForward();

    boolean isShowProgress();

    ReplaySpeed getSpeed();

    HashSet<Player> getViewers();

    HashMap<Integer, Integer> getEntities();

    void addViewers(final Player... players);

    void removeViewers(final Player... players);

    void play();

    void restart();

    void stop();

    void playing(boolean playing);

    void forward(boolean forward);

    void nextSpeed();

    void setSpeed(ReplaySpeed speed);

    void skip(SkipUnit unit, int amount);

    void addIdling(ReplayRunner replayRunner);

    void removeIdling(ReplayRunner replayRunner);

    boolean isInCamera(Player player);

    void joinCamera(Player player, PlayerNpc playerNpc);

    void leaveCamera(Player player);

    AsyncPlayerRunner createAsyncRunner(ReplayPlayer player);

    AsyncPlayerRunner getAsyncRunner(ReplayPlayer player);

    void invite(Player sender, Player target);

    void openSpectatorInventory(Player player);

    void showProgress(boolean show);

    ReplayChunkHandler getReplayChunkHandler();
}

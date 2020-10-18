package eu.mcone.replay.core.api;

import eu.mcone.gameapi.api.game.GameHistory;
import eu.mcone.gameapi.api.game.MessageWrapper;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CoreReplay {

    String getID();

    String getWorld();

    int getLastTick();

    Map<String, List<MessageWrapper>> getMessages();

    GameHistory getGameHistory();

    ReplayPlayer getReplayPlayer(final UUID uuid);

    ReplayPlayer getReplayPlayer(final Player player);

    Collection<ReplayPlayer> getPlayers();

    boolean existsReplayPlayer(final UUID uuid);

    boolean existsReplayPlayer(final Player player);
}

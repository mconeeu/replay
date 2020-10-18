package eu.mcone.replay.core;

import eu.mcone.gameapi.api.GamePlugin;
import eu.mcone.gameapi.api.game.GameHistory;
import eu.mcone.gameapi.api.game.MessageWrapper;
import eu.mcone.gameapi.api.utils.IDUtils;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.entity.Player;

import java.util.*;

public class CoreReplay implements eu.mcone.replay.core.api.CoreReplay {
    @Getter
    private final String ID;
    @Getter
    private final String gameID;

    @Getter
    private final String world;
    @Getter
    private int lastTick;

    //Contains Broadcast messages !
    @Getter
    private final Map<String, List<MessageWrapper>> messages;
    @Getter
    private final Map<String, ReplayPlayer> replayPlayers;

    public CoreReplay(String gameID, String world) {
        this.ID = IDUtils.generateID();

        this.gameID = gameID;
        this.world = world;

        messages = new HashMap<>();
        replayPlayers = new HashMap<>();
    }

    public CoreReplay(String id, String gameID, String world) {
        this.ID = id;

        this.gameID = gameID;
        this.world = world;

        messages = new HashMap<>();
        replayPlayers = new HashMap<>();
    }

    @BsonCreator
    public CoreReplay(@BsonProperty("iD") String ID, @BsonProperty("gameID") String gameID, @BsonProperty("world") String world, @BsonProperty("lastTick") int lastTick,
                      @BsonProperty("messages") Map<String, List<MessageWrapper>> messages, @BsonProperty("replayPlayers") Map<String, ReplayPlayer> replayPlayers) {
        this.ID = ID;
        this.gameID = gameID;
        this.world = world;
        this.lastTick = lastTick;
        this.messages = messages;
        this.replayPlayers = replayPlayers;
    }

    public GameHistory getGameHistory() {
        return GamePlugin.getGamePlugin().getGameHistoryManager().getGameHistory(gameID);
    }

    @BsonIgnore
    public ReplayPlayer getReplayPlayer(final UUID uuid) {
        return replayPlayers.getOrDefault(uuid.toString(), null);
    }

    @BsonIgnore
    public ReplayPlayer getReplayPlayer(final Player player) {
        return getReplayPlayer(player.getUniqueId());
    }

    @BsonIgnore
    public Collection<ReplayPlayer> getPlayers() {
        return new ArrayList<>(replayPlayers.values());
    }

    @BsonIgnore
    public boolean existsReplayPlayer(final UUID uuid) {
        return replayPlayers.containsKey(uuid.toString());
    }

    @BsonIgnore
    public boolean existsReplayPlayer(final Player player) {
        return existsReplayPlayer(player.getUniqueId());
    }
}

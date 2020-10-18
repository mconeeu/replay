package eu.mcone.replay.recorder;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.gameapi.api.GamePlugin;
import eu.mcone.replay.core.api.event.PlayerJoinReplayEvent;
import eu.mcone.replay.core.api.event.PlayerQuitReplayEvent;
import eu.mcone.replay.core.api.exception.ReplayPlayerAlreadyExistsException;
import eu.mcone.replay.core.api.file.ReplayData;
import eu.mcone.replay.core.file.ReplayFile;
import eu.mcone.replay.core.player.ReplayPlayer;
import eu.mcone.replay.core.CoreReplay;
import eu.mcone.replay.recorder.recorder.Recorder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Replay extends CoreReplay implements eu.mcone.replay.recorder.api.Replay {

    private final Map<String, ReplayPlayer> players;
    private final Recorder recorder;
    @Getter
    private final ReplayFile<Replay, ReplayData<?>> replayFile;

    public Replay(String gameID, String world) {
        super(gameID, world);
        players = new HashMap<>();
        recorder = new Recorder(this, ReplayRecorder.getInstance().getReplayManager().getCodecRegistry());
        replayFile = new ReplayFile<>(ReplayRecorder.getInstance(), ReplayRecorder.getInstance().getReplayManager().getCodecRegistry(), this);
    }

    public Replay(String id, String gameID, String world) {
        super(id, gameID, world);
        players = new HashMap<>();
        recorder = new Recorder(this, ReplayRecorder.getInstance().getReplayManager().getCodecRegistry());
        replayFile = new ReplayFile<>(ReplayRecorder.getInstance(), ReplayRecorder.getInstance().getReplayManager().getCodecRegistry(), this);
    }

    public void record() {
        if (!CoreSystem.getInstance().getWorldManager().existsWorldInDatabase(recorder.getWorld())) {
            CoreSystem.getInstance().getWorldManager().upload(CoreSystem.getInstance().getWorldManager().getWorld(recorder.getWorld()), (uploaded) -> {
                if (uploaded) {
                    recorder.record();

                    //Adds the world entity spawn packet
                    for (ReplayPlayer player : players.values()) {
                        Player bukkitPlayer = Bukkit.getPlayer(player.getUuid());
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinReplayEvent(bukkitPlayer));
                    }
                } else {
                    throw new IllegalStateException("[REPLAY] Could not upload World " + recorder.getWorld() + " to database!");
                }
            });
        }
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(GamePlugin.getGamePlugin(), () -> {
            recorder.stop();
            replayFile.save();
            ReplayRecorder.getInstance().getReplayManager().insertReplay(Replay.class, this);
        });
    }

    public void addPlayer(final Player player) {
        try {
            if (!players.containsKey(player.getUniqueId().toString())) {
                players.put(player.getUniqueId().toString(), new eu.mcone.replay.core.player.ReplayPlayer(player));
            } else {
                throw new ReplayPlayerAlreadyExistsException();
            }
        } catch (ReplayPlayerAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    public void removePlayer(final Player player) {
        try {
            if (players.containsKey(player.getUniqueId().toString())) {
                players.get(player.getUniqueId().toString()).setLeft(System.currentTimeMillis() / 1000);
                Bukkit.getServer().getPluginManager().callEvent(new PlayerQuitReplayEvent(player));
            } else {
                throw new NullPointerException("ReplayPlayer " + player.getName() + " doesnt exists!");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

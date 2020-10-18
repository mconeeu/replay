package eu.mcone.replay.core.packets.player.objective;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.event.objective.CoreSidebarObjectiveUpdateEvent;
import eu.mcone.replay.core.api.event.objective.ReplayScoreboardUpdateEvent;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CoreSidebarObjectiveUpdateEventCodec extends Codec<CoreSidebarObjectiveUpdateEvent, ReplayPlayer> {

    public static final byte CODEC_VERSION = 1;

    private Map<Integer, String> scores;

    public CoreSidebarObjectiveUpdateEventCodec() {
        super((byte) 19, (byte) 4);
    }

    @Override
    public Object[] decode(Player player, CoreSidebarObjectiveUpdateEvent objectiveUpdateEvent) {
        scores = objectiveUpdateEvent.getCoreSidebarObjectiveEntry().getScores();
        return new Object[]{objectiveUpdateEvent.getPlayer()};
    }

    @Override
    public void encode(ReplayPlayer replayPlayer) {
        replayPlayer.getScoreboard().setCachedScores(scores);
        replayPlayer.getScoreboard().reload();

        Bukkit.getPluginManager().callEvent(new ReplayScoreboardUpdateEvent(scores));
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeUTF(CoreSystem.getInstance().getGson().toJson(scores));
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        scores = CoreSystem.getInstance().getGson().fromJson(in.readUTF(), HashMap.class);
    }
}

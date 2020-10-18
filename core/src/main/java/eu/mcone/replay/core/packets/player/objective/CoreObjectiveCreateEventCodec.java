package eu.mcone.replay.core.packets.player.objective;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.event.objective.CoreObjectiveCreateEvent;
import eu.mcone.replay.core.api.objective.ReplayPlayerSidebarObjective;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class CoreObjectiveCreateEventCodec extends Codec<CoreObjectiveCreateEvent, ReplayPlayer> {

    public static final byte CODEC_VERSION = 1;

    private String name;

    public CoreObjectiveCreateEventCodec() {
        super((byte) 18, (byte) 4);
    }

    @Override
    public Object[] decode(Player player, CoreObjectiveCreateEvent objectiveCreateEvent) {
        name = objectiveCreateEvent.getSidebarObjective().getName();
        return new Object[]{objectiveCreateEvent.getPlayer()};
    }

    @Override
    public void encode(ReplayPlayer replayPlayer) {
        replayPlayer.setScoreboard(new ReplayPlayerSidebarObjective(name));
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeUTF(name);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        name = in.readUTF();
    }
}

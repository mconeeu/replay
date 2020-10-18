package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.gameapi.api.event.stats.PlayerRoundStatsChangeEvent;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class StatsChangeEventCodec extends Codec<PlayerRoundStatsChangeEvent, ReplayPlayer> {

    public static final byte CODEC_VERSION = 1;

    private int kills;
    private int deaths;
    private int goals;

    public StatsChangeEventCodec() {
        super((byte) 24, (byte) 4);
    }

    @Override
    public Object[] decode(Player player, PlayerRoundStatsChangeEvent statsChangeEvent) {
        kills = statsChangeEvent.getKills();
        deaths = statsChangeEvent.getDeaths();
        goals = statsChangeEvent.getGoals();

        return new Object[]{statsChangeEvent.getPlayer()};
    }

    @Override
    public void encode(ReplayPlayer replayPlayer) {
        replayPlayer.getStats().setKills(kills);
        replayPlayer.getStats().setDeaths(deaths);
        replayPlayer.getStats().setGoals(goals);
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeInt(kills);
        out.writeInt(deaths);
        out.writeInt(goals);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException, ClassNotFoundException {
        kills = in.readInt();
        deaths = in.readInt();
        goals = in.readInt();
    }
}

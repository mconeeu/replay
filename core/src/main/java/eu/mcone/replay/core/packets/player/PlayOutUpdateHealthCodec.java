package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateHealth;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PlayOutUpdateHealthCodec extends Codec<PacketPlayOutUpdateHealth, ReplayPlayer> {

    public static final byte CODEC_VERSION = 1;

    private float health;
    private int food;

    public PlayOutUpdateHealthCodec() {
        super((byte) 26, (byte) 4);
    }

    @Override
    public Object[] decode(Player player, PacketPlayOutUpdateHealth updateHealth) {
        this.health = ReflectionManager.getValue(updateHealth, "a", Float.class);
        this.food = ReflectionManager.getValue(updateHealth, "b", Integer.class);

        return new Object[]{player};
    }

    @Override
    public void encode(ReplayPlayer replayPlayer) {
        replayPlayer.setHealth((int) health);
        replayPlayer.setFood(food);
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeFloat(health);
        out.writeInt(food);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException, ClassNotFoundException {
        health = in.readFloat();
        food = in.readInt();
    }
}

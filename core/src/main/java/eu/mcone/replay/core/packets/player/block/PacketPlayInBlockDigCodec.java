package eu.mcone.replay.core.packets.player.block;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PacketPlayInBlockDigCodec extends Codec<PacketPlayInBlockDig, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private EnumPlayerDigType action;

    public PacketPlayInBlockDigCodec() {
        super((byte) 29, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, PacketPlayInBlockDig blockDig) {
        switch (blockDig.c()) {
            case START_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
                action = EnumPlayerDigType.fromNMS(blockDig.c());
                break;
        }

        return (action != null ? new Object[]{player} : null);
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        switch (action) {
            case START_DESTROY_BLOCK:
                runner.setBreaking(true);
                break;
            case STOP_DESTROY_BLOCK:
                runner.setBreaking(false);
                break;
        }
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeByte(action.getId());
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        action = EnumPlayerDigType.getWhereID(in.readByte());
    }
}

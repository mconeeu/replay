package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import eu.mcone.coresystem.api.bukkit.util.SoundUtils;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PacketPlayOutNamedSoundEffectCodec extends Codec<PacketPlayOutNamedSoundEffect, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private String id;
    private double x;
    private double y;
    private double z;

    public PacketPlayOutNamedSoundEffectCodec() {
        super((byte) 27, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, PacketPlayOutNamedSoundEffect namedSoundEffect) {
        id = ReflectionManager.getValue(namedSoundEffect, "a", String.class);
        x = ReflectionManager.getValue(namedSoundEffect, "b", Integer.class);
        y = ReflectionManager.getValue(namedSoundEffect, "c", Integer.class);
        z = ReflectionManager.getValue(namedSoundEffect, "d", Integer.class);

        return new Object[]{player};
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        CoreLocation location = new CoreLocation(runner.getPlayer().getNpc().getLocation());
        location.setX(x);
        location.setY(y);
        location.setZ(z);

        SoundUtils.playSound(id, location.bukkit(), runner.getViewers().toArray(new Player[0]));
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeUTF(id);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        id = in.readUTF();
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
    }
}

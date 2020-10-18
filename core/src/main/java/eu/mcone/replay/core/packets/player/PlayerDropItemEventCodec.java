package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static eu.mcone.replay.core.packets.player.PacketPlayOutSpawnEntityCodec.getNextEntityId;

@Getter
public class PlayerDropItemEventCodec extends Codec<PlayerDropItemEvent, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private int entityID;
    private double x;
    private double y;
    private double z;

    public PlayerDropItemEventCodec() {
        super((byte) 12, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, PlayerDropItemEvent dropItemEvent) {
        entityID = dropItemEvent.getItemDrop().getEntityId();
        x = dropItemEvent.getItemDrop().getLocation().getX();
        y = dropItemEvent.getItemDrop().getLocation().getY();
        z = dropItemEvent.getItemDrop().getLocation().getZ();
        return new Object[]{dropItemEvent.getPlayer()};
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity();
        int nextID = getNextEntityId();
        ReflectionManager.setValue(spawnEntity, "a", nextID);
        ReflectionManager.setValue(spawnEntity, "b", MathHelper.floor(x * 32.0D));
        ReflectionManager.setValue(spawnEntity, "c", MathHelper.floor(y * 32.0D));
        ReflectionManager.setValue(spawnEntity, "d", MathHelper.floor(z * 32.0D));
        ReflectionManager.setValue(spawnEntity, "h", 0);
        ReflectionManager.setValue(spawnEntity, "i", 0);
        // TODO: Add entities
//        runner.getContainer().getEntities().put(entityID, nextID);

        for (Player player : runner.getViewers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawnEntity);
        }
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeInt(entityID);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException, ClassNotFoundException {
        entityID = in.readInt();
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
    }
}

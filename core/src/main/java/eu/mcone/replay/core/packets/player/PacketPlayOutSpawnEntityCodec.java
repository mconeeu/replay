package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

@Getter
public class PacketPlayOutSpawnEntityCodec extends Codec<PacketPlayOutSpawnEntity, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private int id;
    private int typ; //j
    private int data; //k

    private int x;
    private int y;
    private int z;
    private int yaw;
    private int pitch;

    public PacketPlayOutSpawnEntityCodec() {
        super((byte) 30, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, PacketPlayOutSpawnEntity spawnEntity) {
        typ = ReflectionManager.getValue(spawnEntity, "j", Integer.class);
        EntityType type = EntityType.fromId(typ);
        if (type != null) {
            if (type == EntityType.PLAYER) {
                System.out.println("SPAWN PLAYER");
                id = ReflectionManager.getValue(spawnEntity, "a", Integer.class);
                data = ReflectionManager.getValue(spawnEntity, "k", Integer.class);

                x = ReflectionManager.getValue(spawnEntity, "b", Integer.class);
                y = ReflectionManager.getValue(spawnEntity, "c", Integer.class);
                z = ReflectionManager.getValue(spawnEntity, "d", Integer.class);
                yaw = ReflectionManager.getValue(spawnEntity, "h", Integer.class);
                pitch = ReflectionManager.getValue(spawnEntity, "i", Integer.class);

                return new Object[]{player};
            }
        }

        return null;
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity();
        int id = getNextEntityId();
        ReflectionManager.setValue(spawnEntity, "a", id);
        ReflectionManager.setValue(spawnEntity, "j", typ);
        ReflectionManager.setValue(spawnEntity, "k", data);
        ReflectionManager.setValue(spawnEntity, "b", x);
        ReflectionManager.setValue(spawnEntity, "c", y);
        ReflectionManager.setValue(spawnEntity, "d", z);
        ReflectionManager.setValue(spawnEntity, "h", yaw);
        ReflectionManager.setValue(spawnEntity, "i", pitch);
        // TODO: Add entities
//        runner.getContainer().getEntities().put(this.id, id);

        for (Player player : runner.getViewers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawnEntity);
        }
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeInt(typ);
        out.writeInt(data);
        out.writeInt(x);
        out.writeInt(y);
        out.writeInt(z);
        out.writeInt(yaw);
        out.writeInt(pitch);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        id = in.readInt();
        typ = in.readInt();
        data = in.readInt();
        x = in.readInt();
        y = in.readInt();
        z = in.readInt();
        yaw = in.readInt();
        pitch = in.readInt();
    }

    public static synchronized int getNextEntityId() {
        try {
            Field field = Entity.class.getDeclaredField("entityCount");
            field.setAccessible(true);
            int id = field.getInt(null);
            field.set(null, id + 1);
            return id;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }
}

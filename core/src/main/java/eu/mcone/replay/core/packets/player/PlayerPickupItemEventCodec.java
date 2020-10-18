package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PlayerPickupItemEventCodec extends Codec<PlayerPickupItemEvent, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private int entityID;

    public PlayerPickupItemEventCodec() {
        super((byte) 11, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, PlayerPickupItemEvent pickupItemEvent) {
        entityID = pickupItemEvent.getItem().getEntityId();
        return new Object[]{pickupItemEvent.getPlayer()};
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        if (entityID != 0) {
            System.out.println("ENTITY ID != 0");
            // TODO: Implement entities
//            int id = runner.getContainer().getEntities().getOrDefault(entityID, 0);
//            if (id != 0) {
//                System.out.println("ID != 0");
//                for (Player player : runner.getViewers()) {
//                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(id));
//                }
//            }
        }
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeInt(entityID);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException, ClassNotFoundException {
        entityID = in.readInt();
    }
}

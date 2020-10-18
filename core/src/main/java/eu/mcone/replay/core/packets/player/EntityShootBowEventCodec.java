package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EntityShootBowEventCodec extends Codec<EntityShootBowEvent, ReplayPlayer> {

    public static final byte CODEC_VERSION = 1;

    private double x;
    private double y;
    private double z;

    public EntityShootBowEventCodec() {
        super((byte) 25, (byte) 4);
    }

    @Override
    public Object[] decode(Player player, EntityShootBowEvent packet) {
        Entity entity = packet.getProjectile();

        if (entity instanceof Arrow) {
            if (packet.getEntity() instanceof Player) {
                Arrow arrow = (Arrow) entity;
                x = arrow.getLocation().getX();
                y = arrow.getLocation().getY();
                z = arrow.getLocation().getZ();

                return new Object[]{packet.getEntity()};
            }
        }

        return null;
    }

    @Override
    public void encode(ReplayPlayer encode) {
        Location location = encode.getNpc().getLocation();
        location.setY(1.5);
        encode.getNpc().setBow(false);

        Vector from = new Vector(location.getY(), location.getY(), location.getZ());
        Vector to = new Vector(x, y, z);
        Vector vector = to.subtract(from);

        location.getWorld().spawnArrow(location, vector, (float) 3, (float) 0);
        location.getWorld().playSound(location, Sound.ARROW_HIT, 1, 1);
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException, ClassNotFoundException {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
    }
}

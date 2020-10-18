package eu.mcone.replay.core.packets.player.block;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PlayerInteractEventCodec extends Codec<PlayerInteractEvent, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private Action action;
    private double x;
    private double y;
    private double z;

    public PlayerInteractEventCodec() {
        super((byte) 20, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, PlayerInteractEvent interactEvent) {
        if (interactEvent.getAction().equals(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)
                && interactEvent.getClickedBlock().getType().equals(Material.TNT)
                && interactEvent.getPlayer().getItemInHand().getType().equals(Material.FLINT_AND_STEEL)) {
            x = interactEvent.getClickedBlock().getLocation().getX();
            x = interactEvent.getClickedBlock().getLocation().getY();
            z = interactEvent.getClickedBlock().getLocation().getZ();
            action = Action.fromNMS(interactEvent.getAction());
            return new Object[]{interactEvent.getPlayer()};
        }

        return null;
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        if (action != null && runner.getPlayer() != null) {
            if (action == Action.RIGHT_CLICK_BLOCK) {
                CoreLocation location = new CoreLocation(runner.getPlayer().getNpc().getLocation());
                location.setX(x);
                location.setZ(y);
                location.setZ(z);

                EntityTNTPrimed tnt = new EntityTNTPrimed(((CraftWorld) location.bukkit().getWorld()).getHandle());
                tnt.setPosition(location.getX(), location.getY(), location.getZ());
                PacketPlayOutSpawnEntity spawnEntity = new PacketPlayOutSpawnEntity(tnt, 50);

                for (Player player : runner.getViewers()) {
                    player.sendBlockChange(location.bukkit(), Material.AIR, (byte) 0);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawnEntity);
                }
            }
        }
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeByte(action.getId());
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        action = Action.getWhereID(in.readByte());
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
    }
}

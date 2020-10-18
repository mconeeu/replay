package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class EntityRespawnEventCodec extends Codec<PlayerRespawnEvent, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private double x;
    private double y;
    private double z;

    public EntityRespawnEventCodec() {
        super((byte) 14, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, PlayerRespawnEvent playerRespawnEvent) {
        x = playerRespawnEvent.getPlayer().getLocation().getX();
        y = playerRespawnEvent.getPlayer().getLocation().getY();
        z = playerRespawnEvent.getPlayer().getLocation().getZ();
        return new Object[]{playerRespawnEvent.getPlayer()};
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        runner.getPlayer().getNpc().setItemInHand(null);
        runner.getPlayer().getNpc().togglePlayerVisibility(ListMode.WHITELIST, runner.getViewers().toArray(new Player[0]));
        CoreLocation location = new CoreLocation(runner.getPlayer().getNpc().getLocation());
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        runner.getPlayer().getNpc().teleport(location);
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

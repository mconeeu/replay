package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.event.npc.NpcAnimationStateChangeEvent;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PlayerJoinReplayEventCodec extends Codec<PlayerJoinEvent, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public PlayerJoinReplayEventCodec() {
        super((byte) 7, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, PlayerJoinEvent playerJoin) {
        this.world = playerJoin.getPlayer().getLocation().getWorld().getName();
        this.x = playerJoin.getPlayer().getLocation().getX();
        this.y = playerJoin.getPlayer().getLocation().getY();
        this.z = playerJoin.getPlayer().getLocation().getZ();
        this.yaw = playerJoin.getPlayer().getLocation().getYaw();
        pitch = playerJoin.getPlayer().getLocation().getPitch();

        return new Object[]{playerJoin.getPlayer()};
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        runner.getPlayer().getNpc().teleport(getLocation());
        runner.getPlayer().getNpc().togglePlayerVisibility(ListMode.WHITELIST, runner.getViewers().toArray(new Player[0]));
        Bukkit.getPluginManager().callEvent(new NpcAnimationStateChangeEvent(runner.getPlayer().getNpc(), NpcAnimationStateChangeEvent.NpcAnimationState.START));
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeUTF(world);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeFloat(yaw);
        out.writeFloat(pitch);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException, ClassNotFoundException {
        world = in.readUTF();
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
        yaw = in.readFloat();
        pitch = in.readFloat();
    }

    private CoreLocation getLocation() {
        return new CoreLocation(world, x, y, z, yaw, pitch);
    }
}

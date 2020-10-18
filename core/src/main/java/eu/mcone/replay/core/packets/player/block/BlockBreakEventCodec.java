package eu.mcone.replay.core.packets.player.block;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class BlockBreakEventCodec extends Codec<BlockBreakEvent, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private double x;
    private double y;
    private double z;
    private String world;

    public BlockBreakEventCodec() {
        super((byte) 9, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, BlockBreakEvent blockBreakEvent) {
        Block block = blockBreakEvent.getBlock();
        x = block.getX();
        y = block.getY();
        z = block.getZ();
        world = block.getWorld().getName();
        return new Object[]{blockBreakEvent.getPlayer()};
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        for (Player player : runner.getViewers()) {
            if (player.getLocation().getWorld().getName().equalsIgnoreCase(world)) {
                player.sendBlockChange(getLocation().bukkit(), Material.AIR, (byte) 0);
            }
        }
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeUTF(world);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
        world = in.readUTF();
    }

    private CoreLocation getLocation() {
        return new CoreLocation(world, x, y, z, 0, 0);
    }
}

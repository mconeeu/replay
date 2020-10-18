package eu.mcone.replay.core.packets.player.block;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class BlockPlaceEventCodec extends Codec<BlockPlaceEvent, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private int id;
    private byte subID;

    private double x;
    private double y;
    private double z;
    private String world;

    public BlockPlaceEventCodec() {
        super((byte) 10, (byte) 3);
    }

    @BsonIgnore
    public ItemStack getItemStack() {
        return new ItemBuilder(Material.getMaterial(id), 1, subID).create();
    }

    @Override
    public Object[] decode(Player player, BlockPlaceEvent blockPlaceEvent) {
        Block block = blockPlaceEvent.getBlock();

        id = block.getType().getId();
        subID = block.getData();

        x = block.getLocation().getX();
        y = block.getLocation().getY();
        z = block.getLocation().getZ();
        world = block.getLocation().getWorld().getName();
        return new Object[]{blockPlaceEvent.getPlayer()};
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        for (Player player : runner.getViewers()) {
            if (player.getLocation().getWorld().getName().equalsIgnoreCase(world)) {
                player.sendBlockChange(getLocation().bukkit(), Material.getMaterial(id), subID);
            }
        }
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeByte(subID);

        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(z);
        out.writeUTF(world);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        id = in.readInt();
        subID = in.readByte();

        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
        world = in.readUTF();
    }

    private CoreLocation getLocation() {
        return new CoreLocation(world, x, y, z, 0, 0);
    }
}

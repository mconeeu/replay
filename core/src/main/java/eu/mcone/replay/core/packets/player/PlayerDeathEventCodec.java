package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PlayerDeathEventCodec extends Codec<PlayerDeathEvent, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    public PlayerDeathEventCodec() {
        super((byte) 13, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, PlayerDeathEvent playerDeathEvent) {
        return new Object[]{playerDeathEvent.getEntity()};
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        runner.getPlayer().getNpc().togglePlayerVisibility(ListMode.BLACKLIST, runner.getViewers().toArray(new Player[0]));
    }

    @Override
    protected void onWriteObject(DataOutputStream dataOutputStream) throws IOException {

    }

    @Override
    protected void onReadObject(DataInputStream dataInputStream) throws IOException, ClassNotFoundException {

    }
}

package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MobEffect;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEffect;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class PacketPlayOutEntityEffectCodec extends Codec<PacketPlayOutEntityEffect, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private byte typ;
    private byte amplifier;

    public PacketPlayOutEntityEffectCodec() {
        super((byte) 28, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, PacketPlayOutEntityEffect entityEffect) {
        typ = ReflectionManager.getValue(entityEffect, "b", Byte.class);
        amplifier = ReflectionManager.getValue(entityEffect, "c", Byte.class);

        return new Object[]{player};
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        for (PotionEffect effect : getPotion().getEffects()) {
            runner.getPlayer().getNpc().addPotionEffect(new MobEffect(typ, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles()), runner.getViewers().toArray(new Player[0]));
        }
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeByte(typ);
        out.writeByte(amplifier);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        typ = in.readByte();
        amplifier = in.readByte();
    }

    public Potion getPotion() {
        return new Potion(PotionType.getByEffect(PotionEffectType.getById(typ)), 2);
    }
}

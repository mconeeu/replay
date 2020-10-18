package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.npc.entity.EntityProjectile;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import lombok.Getter;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class ProjectileLaunchEventCodec extends Codec<ProjectileLaunchEvent, PlayerNpc> {

    public static final byte CODEC_VERSION = 1;

    private int id;

    public ProjectileLaunchEventCodec() {
        super((byte) 22, (byte) 2);
    }

    @Override
    public Object[] decode(Player player, ProjectileLaunchEvent launchEvent) {
        Projectile projectile = launchEvent.getEntity();
        if (projectile instanceof Snowball) {
            Snowball snowball = (Snowball) projectile;

            if (snowball.getShooter() instanceof Player) {
                Player shooter = (Player) snowball.getShooter();
                id = EntityProjectile.SNOWBALL.getId();
                return new Object[]{shooter};
            }
        } else if (projectile instanceof Egg) {
            Egg egg = (Egg) projectile;
            if (egg.getShooter() instanceof Player) {
                Player shooter = (Player) egg.getShooter();
                id = EntityProjectile.EGG.getId();
                return new Object[]{shooter};
            }
        }

        return null;
    }

    @Override
    public void encode(PlayerNpc playerNpc) {
        playerNpc.throwProjectile(getProjectile(), playerNpc.getLocation().getDirection());
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeInt(id);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException, ClassNotFoundException {
        id = in.readInt();
    }

    private EntityProjectile getProjectile() {
        for (EntityProjectile projectile : EntityProjectile.values()) {
            if (projectile.getId() == id) {
                return projectile;
            }
        }

        return null;
    }
}

package eu.mcone.replay.viewer.npc;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.npc.data.PlayerNpcData;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NpcUtils {

    public static PlayerNpc constructNpcForPlayer(final ReplayPlayer player, final UUID containerUUID, Player... viewers) {
        return (PlayerNpc) CoreSystem.getInstance().getNpcManager().addNPC(new NpcData(
                EntityType.PLAYER,
                containerUUID + "_" + player.getName(),
                (player.isReported() ? "§8[§cR§8] " + player.getName() : player.getName() + "_1"),
                player.getSpawnLocation(),
                new PlayerNpcData
                        (
                                player.getName(),
                                //TODO: Insert tab prefix
                                (player.isReported() ? "§8[§cR§8] " + player.getName() : player.getName() + "_1"),
                                SkinInfo.SkinType.PLAYER,
                                false,
                                false,
                                false,
                                null
                        )
        ), ListMode.WHITELIST, viewers);
    }
}
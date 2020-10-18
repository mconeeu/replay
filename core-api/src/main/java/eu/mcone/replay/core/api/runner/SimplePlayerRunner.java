package eu.mcone.replay.core.api.runner;

import eu.mcone.replay.core.api.player.ReplayPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface SimplePlayerRunner {

    ReplayPlayer getPlayer();

    void setBreaking(boolean breaking);

    Collection<Player> getViewers();
}

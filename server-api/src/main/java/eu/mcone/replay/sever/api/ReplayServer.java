package eu.mcone.replay.sever.api;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class ReplayServer extends CorePlugin {

    @Getter
    private static ReplayServer instance;

    protected ReplayServer() {
        super("replay_server", ChatColor.RED, "replay.prefix");
        instance = this;
    }

    public abstract CoreWorld getReplayWorld();

    public abstract BuildSystem getBuildSystem();

    public abstract List<Player> getViewers();
}

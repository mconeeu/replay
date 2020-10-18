package eu.mcone.replay.viewer.world;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.gameapi.api.GameAPI;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.mongodb.client.model.Projections.include;

public class WorldDownloader
//        implements eu.mcone.gameapi.api.replay.world.WorldDownloader
        // TODO: implement new world management
{

    @Getter
    private final List<String> downloaded;
    private BukkitTask task;

    public WorldDownloader() {
        this.downloaded = new ArrayList<>();
    }

    public void runDownloader() {
        task = Bukkit.getScheduler().runTaskTimer(GameAPI.getInstance(), () -> {
            for (Document entry : CoreSystem.getInstance().getMongoDB().getCollection("replay_sessions", Document.class).find().projection(include("info.world"))) {
                String world = entry.get("info", Document.class).getString("world");

                if (!downloaded.contains(world)) {
                    if (!CoreSystem.getInstance().getWorldManager().existWorld(world)) {
                        GameAPI.getInstance().sendConsoleMessage("§aDownloading World " + world + " from database...");
                        CoreSystem.getInstance().getWorldManager().download(world, (succeeded) -> {
                            if (succeeded) {
                                CoreWorld downloadedWorld = CoreSystem.getInstance().getWorldManager().getWorld(world);
                                downloadedWorld.setLoadOnStartup(false);
                                downloadedWorld.save();
                            } else {
                                GameAPI.getInstance().sendConsoleMessage("§cDie Welt " + world + " konnte nicht heruntergeladen werden.");
                            }
                        });
                    }
                }
            }
        }, 20 * 60, 20 * 60);
    }

    public Collection<String> getDownloadedWorlds() {
        return downloaded;
    }

    public void stop() {
        if (task != null)
            Bukkit.getScheduler().cancelTask(task.getTaskId());
    }
}

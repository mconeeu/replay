package eu.mcone.replay.sever;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.replay.sever.command.ReplayCMD;
import eu.mcone.replay.sever.listener.*;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ReplayServer extends eu.mcone.replay.sever.api.ReplayServer {

    @Getter
    private CoreWorld replayWorld;

    @Getter
    private List<Player> viewer;

    @Getter
    private BuildSystem buildSystem;


    @Override
    public void onEnable() {
        withErrorLogging(() -> {
            viewer = new ArrayList<>();

            sendConsoleMessage("§aStarting plugin...");
            registerListeners();
            registerCommands();

            replayWorld = CoreSystem.getInstance().getWorldManager().getWorld("Lobby-Replay");

            sendConsoleMessage("§aInitializing Build-System...");
            buildSystem = CoreSystem.getInstance().initialiseBuildSystem(BuildSystem.BuildEvent.BLOCK_BREAK, BuildSystem.BuildEvent.BLOCK_PLACE);

            CoreSystem.getInstance().setPlayerChatEnabled(false);

            sendConsoleMessage("§aVersion §f" + this.getDescription().getVersion() + "§a enabled...");
        });
    }

    @Override
    public void onDisable() {
        withErrorLogging(() -> {

        });
    }

    private void registerListeners() {
        registerEvents(
                new PlayerJoinListener(),
                new PlayerQuitListener(),
                new NpcInteractListener(),
                new PlayerInteractListener(),
                new InventoryClickListener(),
                new ItemListener(),
                new DamageListener(),
                new GeneralReplayListener(),
                new WeatherListener(),
                new PlayerListener(),
                new MobSpawningListener(),
                new PlayerAsyncChatListener()
        );
    }

    private void registerCommands() {
        registerCommands(
                new ReplayCMD()
        );
    }

    @Override
    public List<Player> getViewers() {
        return viewer;
    }
}

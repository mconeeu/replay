package eu.mcone.replay.sever.objectives;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreSidebarObjective;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreSidebarObjectiveEntry;
import eu.mcone.gameapi.api.GamePlugin;
import eu.mcone.replay.sever.ReplayServer;
import eu.mcone.replay.viewer.api.ReplayViewer;

public class MainReplayObjective extends CoreSidebarObjective {

    public MainReplayObjective() {
        super("REPLAY_SERVER");
    }

    @Override
    protected void onRegister(CorePlayer corePlayer, CoreSidebarObjectiveEntry entry) {
        entry.setTitle("§e§lReplay Server");
        entry.setScore(5, "");
        entry.setScore(4, "§8» §7Replays:");
        entry.setScore(3, "§f§l" + ReplayViewer.getInstance().getReplayManager().getReplaySize());
        entry.setScore(2, "");
        entry.setScore(1, "§f§lMCONE.EU ");
    }

    @Override
    protected void onReload(CorePlayer corePlayer, CoreSidebarObjectiveEntry coreSidebarObjectiveEntry) {

    }
}

package eu.mcone.replay.recorder.manager;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.replay.core.manager.CoreReplayManager;
import eu.mcone.replay.recorder.api.Replay;

import java.util.ArrayList;
import java.util.List;

public class ReplayManager extends CoreReplayManager implements eu.mcone.replay.recorder.api.manager.ReplayManager {

    private final List<Replay> replays;

    public ReplayManager() {
        replays = new ArrayList<>();
    }

    public Replay createReplay(String gameID, String world) {
        Replay replay = new eu.mcone.replay.recorder.Replay(gameID, world);
        replays.add(replay);
        return replay;
    }

    public Replay createReplay(String ID, String gameID, String world) {
        Replay replay = new eu.mcone.replay.recorder.Replay(ID, gameID, world);
        replays.add(replay);
        return replay;
    }

    public void registerReportMethod() {
        CoreSystem.getInstance().getOverwatch().getReportManager().setReportMethod((manager, reporter, reported, reportReason) -> {
            CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(reporter);
            if (replays.size() > 0) {
                return new Report(reported.getUniqueId(), reporter.getUniqueId(), reportReason, corePlayer.getTrust().getGroup().getTrustPoints(), replays.get(0).getID());
            } else {
                return new Report(reported.getUniqueId(), reporter.getUniqueId(), reportReason, corePlayer.getTrust().getGroup().getTrustPoints());
            }
        });
    }

    public List<Replay> getLoadedReplays() {
        return replays;
    }
}

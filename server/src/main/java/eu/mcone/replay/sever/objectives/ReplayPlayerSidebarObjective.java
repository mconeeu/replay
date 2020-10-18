package eu.mcone.replay.sever.objectives;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreSidebarObjective;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreSidebarObjectiveEntry;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class ReplayPlayerSidebarObjective extends CoreSidebarObjective {

    @Setter
    private Map<Integer, String> cachedScores;

    public ReplayPlayerSidebarObjective(String name) {
        super(name);
        cachedScores = new HashMap<>();
    }

    @Override
    protected void onRegister(CorePlayer corePlayer, CoreSidebarObjectiveEntry coreSidebarObjectiveEntry) {
    }

    @Override
    protected void onReload(CorePlayer corePlayer, CoreSidebarObjectiveEntry entry) {
        entry.setScores(cachedScores);
    }

    public void setScore(int score, String text) {
        cachedScores.put(score, text);
    }
}

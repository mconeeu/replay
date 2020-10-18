package eu.mcone.replay.sever.objectives;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreSidebarObjective;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreSidebarObjectiveEntry;
import eu.mcone.gameapi.api.game.GameHistory;
import eu.mcone.replay.viewer.api.Replay;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReplayObjective extends CoreSidebarObjective {

    private final Replay replay;

    public ReplayObjective(final Replay replay) {
        super("REPLAY");
        this.replay = replay;
    }

    @Override
    protected void onRegister(CorePlayer corePlayer, CoreSidebarObjectiveEntry entry) {
        GameHistory gameHistory = replay.getGameHistory();
        Gamemode gamemode = (gameHistory.getGamemode() != null && gameHistory.getGamemode() != null ? gameHistory.getGamemode() : null);

        setDisplayName("§e§lReplay Server");
        entry.setScore(15, "");
        entry.setScore(14, "§8» §7ReplayID:");
        entry.setScore(13, "§f   " + replay.getID());
        entry.setScore(12, "§8» §7Spielmodus:");
        entry.setScore(11, "§f   " + (gamemode != null ? gamemode.getColor() + gamemode.getName() : "§c✘"));
        entry.setScore(10, "§8» §7Vom:");
        entry.setScore(9, "§f   " + new SimpleDateFormat("dd.MM.yyyy").format(new Date(gameHistory.getStopped() * 1000)));
        entry.setScore(8, "§8» §7Gestartet:");
        entry.setScore(7, "§f   " + convertDate(gameHistory.getStarted()));
        entry.setScore(6, "§8» §7Gestopt:");
        entry.setScore(5, "§f   " + convertDate(gameHistory.getStopped()));
        entry.setScore(4, "§8» §7Spieler:");
        entry.setScore(3, "§f   " + replay.getPlayers().size());
        entry.setScore(2, "§8» §7Gewinner:");
        entry.setScore(1, "§f   " + (gameHistory.getWinner() != null ? gameHistory.getWinner() : "§c✘"));
    }

    @Override
    protected void onReload(CorePlayer corePlayer, CoreSidebarObjectiveEntry coreSidebarObjectiveEntry) {

    }

    private String convertDate(final long timestamp) {
        return new SimpleDateFormat("HH:mm").format(new Date(timestamp * 1000));
    }
}

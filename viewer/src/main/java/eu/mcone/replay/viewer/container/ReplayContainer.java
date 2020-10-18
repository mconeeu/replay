package eu.mcone.replay.viewer.container;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.replay.core.api.chunk.ReplayChunkHandler;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.viewer.Replay;
import eu.mcone.replay.viewer.ReplayViewer;
import eu.mcone.replay.viewer.api.event.PlayerJoinReplayCameraEvent;
import eu.mcone.replay.viewer.api.event.PlayerLeaveReplayCameraEvent;
import eu.mcone.replay.viewer.api.event.runner.*;
import eu.mcone.replay.viewer.api.runner.PlayerRunner;
import eu.mcone.replay.viewer.api.runner.ReplayRunner;
import eu.mcone.replay.viewer.api.runner.ReplaySpeed;
import eu.mcone.replay.viewer.api.utils.SkipUnit;
import eu.mcone.replay.viewer.inventory.ReplayInformationInventory;
import eu.mcone.replay.viewer.inventory.ReplaySpectatorInventory;
import eu.mcone.replay.viewer.listener.NPCInteractListener;
import eu.mcone.replay.viewer.npc.NpcUtils;
import eu.mcone.replay.viewer.runner.player.AsyncPlayerRunner;
import eu.mcone.replay.viewer.runner.player.SyncPlayerRunner;
import eu.mcone.replay.viewer.runner.server.SyncServerRunner;
import lombok.Getter;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class ReplayContainer implements eu.mcone.replay.viewer.api.container.ReplayContainer {

    @Getter
    private final Replay replay;

    @Getter
    private final UUID containerUUID;
    @Getter
    private int tick;
    @Getter
    private boolean playing = false;
    @Getter
    private boolean forward = true;
    @Getter
    private boolean showProgress = true;
    @Getter
    private ReplaySpeed speed = ReplaySpeed._1X;

    private int skipped;

    @Getter
    private final HashSet<Player> viewers;
    @Getter
    private final HashMap<Player, PlayerNpc> inCamera;
    private final SyncServerRunner serverRunner;
    private final HashMap<UUID, AsyncPlayerRunner> asyncRunner;
    private final HashMap<UUID, PlayerRunner> running;
    private final HashSet<ReplayRunner> idling;

    @Getter
    private NPCInteractListener npcListener;

    @Getter
    private final HashMap<Integer, Integer> entities;
    private final ReplayContainerListener replayContainerListener;

    private BukkitTask progressTask;

    public ReplayContainer(final Replay replay) {
        this.replay = replay;
        this.containerUUID = UUID.randomUUID();
//        tick = new AtomicInteger();
        viewers = new HashSet<>();
        inCamera = new HashMap<>();
        serverRunner = new SyncServerRunner(this);
        asyncRunner = new HashMap<>();
        running = new HashMap<>();
        idling = new HashSet<>();
        entities = new HashMap<>();
        replayContainerListener = new ReplayContainerListener(this);
        CoreSystem.getInstance().registerEvents(replayContainerListener);
    }

    public void addViewers(final Player... players) {
        if (!playing) {
            //Check if the loading is to slow
            replay.getReplayFile().getReplayChunkHandler().preLoad();
        }

        for (Player player : players) {
            viewers.add(player);

            Bukkit.getPluginManager().callEvent(new ReplayJoinEvent(player, this));

//            if (ReplayViewer.hasOption(Option.USE_REPLAY_VIEW_MANAGER)) {
//                ReplayViewer.getInstance().getReplayViewManager().joinReplay(player, replay);
//            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(all);
                all.hidePlayer(player);
            }

            for (Player watcher : viewers) {
                player.showPlayer(watcher);
                watcher.showPlayer(player);
            }

            for (PlayerRunner runner : running.values()) {
                runner.getPlayer().getNpc().toggleVisibility(player, false);
            }
        }
    }

    public void removeViewers(final Player... players) {
        for (Player player : players) {
//            if (GamePlugin.getGamePlugin().hasOption(Option.USE_REPLAY_VIEW_MANAGER)) {
//                GamePlugin.getGamePlugin().getReplayManager().getReplayViewManager().leaveReplay(player);
//            }

            viewers.remove(player);

            for (Map.Entry<Player, PlayerNpc> inCamera : inCamera.entrySet()) {
                inCamera.getValue().setCamera(inCamera.getKey(), false);
            }

            inCamera.clear();

            for (PlayerRunner runner : running.values()) {
                runner.getPlayer().getNpc().toggleVisibility(player, true);
            }

            for (Player watcher : viewers) {
                watcher.hidePlayer(player);
                player.hidePlayer(watcher);
            }

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!viewers.contains(all)) {
                    player.showPlayer(all);
                    all.showPlayer(all);
                }
            }

            Bukkit.getPluginManager().callEvent(new ReplayQuitEvent(player, this));

            if (viewers.isEmpty()) {
                if (playing && running.size() > 0) {
                    serverRunner.stop();
                    serverRunner.setSpeed(null);

                    forward = true;
                    speed = null;
                    playing = false;

                    npcListener.setUnregister(true);
                    running.clear();
                    Bukkit.getScheduler().cancelTask(progressTask.getTaskId());
                    Bukkit.getPluginManager().callEvent(new ReplayStopEvent(replay.getID(), getContainerUUID(), tick));
                    tick = 0;
                }

                replay.removeContainer(containerUUID);
            }
        }
    }

    private void progress() {
        progressTask = Bukkit.getScheduler().runTaskTimer(ReplayViewer.getInstance(), () -> {
            if (playing && showProgress) {
                if (tick > replay.getLastTick()) {
                    stop();
                } else {
                    if (idling.size() == 0) {
                        int repeat;
                        if (speed != null && skipped == speed.getWait()) {
                            repeat = (speed.isAdd() ? 2 : 0);
                            skipped = 0;
                        } else {
                            repeat = 1;
                        }

                        for (int i = 0; i < repeat; i++) {
                            if (forward) {
                                tick++;
                            } else {
                                tick--;
                            }

                            for (Player viewer : viewers) {
                                CoreSystem.getInstance().createActionBar().message(ReplayInformationInventory.getLength(tick)).send(viewer);
                            }
                        }
                    }
                }
            } else {
                for (Player viewer : viewers) {
                    CoreSystem.getInstance().createActionBar().message("§7Paussiert").send(viewer);
                }
            }
        }, 1L, 1L);
    }

    public void play() {
        if (!playing && running.size() == 0 && viewers.size() > 0) {
            playing = true;

            for (ReplayPlayer player : replay.getPlayers()) {
                if (!(running.containsKey(player.getUuid()))) {
                    player.setNpc(NpcUtils.constructNpcForPlayer(player, containerUUID, viewers.toArray(new Player[viewers.size() - 1])));
                    PlayerRunner runner = new SyncPlayerRunner(player, this);
                    runner.play();
                    running.put(player.getUuid(), runner);
                }
            }

            serverRunner.play();
            progress();
            npcListener = new NPCInteractListener(this);
            Bukkit.getPluginManager().callEvent(new ReplayStartEvent(this, replay));
        }
    }

    public void restart() {
        if (this.playing) {
            this.speed = null;
            this.tick = 0;
        } else {
            this.forward = true;
            this.play();
        }
    }

    public void stop() {
        if (playing && running.size() > 0) {
            serverRunner.stop();
            serverRunner.setSpeed(null);

            forward = true;
            speed = null;
            playing = false;

            npcListener.setUnregister(true);
            running.clear();
            Bukkit.getScheduler().cancelTask(progressTask.getTaskId());
            Bukkit.getPluginManager().callEvent(new ReplayStopEvent(replay.getID(), getContainerUUID(), tick));
            tick = 0;
            removeViewers(viewers.toArray(new Player[0]));
        }
    }

    public void playing(boolean playing) {
        this.playing = playing;
        if (playing) {
            for (PlayerRunner runner : running.values()) {
                runner.play();
            }

            serverRunner.play();
            Bukkit.getPluginManager().callEvent(new ReplayStartPlayingEvent(this, tick));
        } else {
            for (PlayerRunner runner : running.values()) {
                runner.stop();
            }

            serverRunner.stop();
            Bukkit.getPluginManager().callEvent(new ReplayStopPlayingEvent(this, tick));
        }
    }

    public void forward(boolean forward) {
        this.forward = forward;
    }

    public void nextSpeed() {
        if (playing) {
            ReplaySpeed next = speed.getNextSpeed();
            ReplayChangeSpeedEvent changeSpeedEvent = new ReplayChangeSpeedEvent(replay.getID(), this, this.speed, next);
            Bukkit.getPluginManager().callEvent(changeSpeedEvent);

            if (!changeSpeedEvent.isCancelled()) {
                this.speed = next;
                serverRunner.setSpeed(speed);
            }
        }
    }

    public void setSpeed(ReplaySpeed speed) {
        if (playing) {
            ReplayChangeSpeedEvent changeSpeedEvent = new ReplayChangeSpeedEvent(replay.getID(), this, this.speed, speed);
            Bukkit.getPluginManager().callEvent(changeSpeedEvent);

            if (!changeSpeedEvent.isCancelled()) {
                this.speed = speed;

                serverRunner.setSpeed(speed);
            }
        }
    }

    public void skip(SkipUnit unit, int amount) {
        if (playing) {
            int converted = ReplayRunner.convertToTicks(unit, amount);
            boolean isNegative = converted < 0;
            int lastTick = (isNegative ? tick - converted : tick + converted);
            ReplaySkipEvent skipEvent = new ReplaySkipEvent(replay.getID(), containerUUID);
            Bukkit.getPluginManager().callEvent(skipEvent);

            if (!skipEvent.isCancelled()) {
                for (PlayerRunner runner : running.values()) {
                    runner.skip(unit, amount);
                }

                serverRunner.skip(unit, amount);

                if (lastTick < replay.getLastTick() && lastTick > 0) {
                    tick = lastTick;
                } else {
                    tick = 0;
                }
            }
        }
    }

    public void addIdling(ReplayRunner playerRunner) {
        idling.add(playerRunner);
    }

    public void removeIdling(ReplayRunner playerRunner) {
        idling.remove(playerRunner);
    }

    public boolean isInCamera(Player player) {
        return inCamera.containsKey(player);
    }

    public void joinCamera(Player player, PlayerNpc playerNpc) {
        Bukkit.getPluginManager().callEvent(new PlayerJoinReplayCameraEvent(this, player, playerNpc));
        playerNpc.setCamera(player, true);
        inCamera.put(player, playerNpc);
    }

    public void leaveCamera(Player player) {
        Bukkit.getPluginManager().callEvent(new PlayerLeaveReplayCameraEvent(this, player, inCamera.get(player)));
        inCamera.get(player).setCamera(player, false);
        inCamera.remove(player);
    }


    public AsyncPlayerRunner createAsyncRunner(final ReplayPlayer player) {
        if (!asyncRunner.containsKey(player.getUuid())) {
            AsyncPlayerRunner runner = new eu.mcone.replay.viewer.runner.player.AsyncPlayerRunner(player, this);
            asyncRunner.put(player.getUuid(), runner);
            return runner;
        } else {
            return asyncRunner.get(player.getUuid());
        }
    }

    public AsyncPlayerRunner getAsyncRunner(ReplayPlayer player) {
        return asyncRunner.get(player.getUuid());
    }

    public void invite(final Player sender, final Player target) {
        CorePlayer corePlayer = CoreSystem.getInstance().getCorePlayer(sender);
        CorePlayer targetCorePlayer = CoreSystem.getInstance().getCorePlayer(target);

        if (corePlayer != null && targetCorePlayer != null) {
            if (viewers.contains(sender) && !viewers.contains(target)) {
                ReplayInvitePlayerEvent invitePlayerEvent = new ReplayInvitePlayerEvent(corePlayer, targetCorePlayer, replay.getID(), this);
                Bukkit.getPluginManager().callEvent(invitePlayerEvent);

                if (!invitePlayerEvent.isCancelled()) {
                    TextComponent tp = new TextComponent("§7Der Spieler §e" + sender.getName() + " §7lädt dich in ein Replay (" + replay.getID() + ") §7ein.");
                    tp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§lReplay beitreten \n§7§oLinksklick zum beitreten").create()));
                    tp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/replay join " + sender.getName()));

                    ReplayViewer.getInstance().getMessenger().send(target, new BaseComponent[]{tp});
                    ReplayViewer.getInstance().getMessenger().sendSender(sender, "§aDu hast den Spieler §e" + target.getName() + " §aerfolgreich eingeladen.");
                }
            } else {
                ReplayViewer.getInstance().getMessenger().sendSender(sender, "§cDer Spieler ist bereits in deiner Session.");
            }
        }
    }

    public void openSpectatorInventory(Player player) {
        if (viewers.contains(player))
            new ReplaySpectatorInventory(replay, player);
    }

    public void showProgress(boolean show) {
        this.showProgress = show;
    }

    public ReplayChunkHandler getReplayChunkHandler() {
        return getReplay().getReplayFile().getReplayChunkHandler();
    }
}

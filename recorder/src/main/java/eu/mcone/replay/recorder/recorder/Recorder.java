package eu.mcone.replay.recorder.recorder;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.codec.CodecListener;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.replay.core.chunk.ReplayChunk;
import eu.mcone.replay.core.CoreReplay;
import eu.mcone.replay.recorder.ReplayRecorder;
import group.onegaming.networkmanager.core.api.util.IDUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

@Getter
public class Recorder extends eu.mcone.coresystem.api.bukkit.npc.capture.Recorder implements eu.mcone.replay.recorder.api.recorder.Recorder {

    private final HashMap<Integer, eu.mcone.replay.core.api.chunk.ReplayChunk> chunks;
    private int lastTick;

    private BukkitTask task;

    private final CoreReplay replay;
    private final CodecRegistry registry;
    private CodecListener codecListener;

    public Recorder(CoreReplay replay, CodecRegistry registry) {
        super(IDUtils.generateID(), replay.getWorld());
        this.replay = replay;
        chunks = new HashMap<>();
        this.registry = registry;
    }

    @Override
    public void record() {
        registry.listeningForCodecs(true);
        started = System.currentTimeMillis() / 1000;
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreSystem.getInstance(), () -> ticks++, 1L, 1L);

        Bukkit.getScheduler().runTaskAsynchronously(ReplayRecorder.getInstance(), () -> {
            this.codecListener = (codec, objects) -> {
                if (objects != null) {
                    System.out.println(codec.getClass().getSimpleName());
                    Class<?> trigger = registry.getTriggerClass(codec.getCodecID());
                    if (trigger != null) {
                        if (registry.getTriggerClass(codec.getCodecID()).equals(PlayerMoveEvent.class)) {
                            if ((ticks % 2) == 0) {
                                addCodec((Player) objects[0], codec);
                            }
                        } else {
                            addCodec((Player) objects[0], codec);
                        }
                    }
                } else {
                    addServerCodec(codec);
                }
            };

            registry.registerCodecListener(codecListener);
        });
    }

    private void addServerCodec(Codec<?, ?> codec) {
        int chunkID = ticks / 600;
        lastTick = ticks;

        if (chunks.containsKey(chunkID)) {
            chunks.get(chunkID).addServerCodec(ticks, codec);
        } else {
            ReplayChunk chunk = new eu.mcone.replay.core.chunk.ReplayChunk(chunkID, new ReplayChunk.ChunkData(this.registry));
            chunk.addServerCodec(ticks, codec);
            chunks.put(chunkID, chunk);
        }
    }

    private void addCodec(Player player, Codec<?, ?> codec) {
        if (replay.existsReplayPlayer(player)) {
            int chunkID = ticks / 600;
            lastTick = ticks;

            if (chunks.containsKey(chunkID)) {
                System.out.println("ADD CODEC " + codec.getClass().getSimpleName());
                chunks.get(chunkID).addPlayerCodec(player.getUniqueId(), ticks, codec);
            } else {
                System.out.println("NEW REPLAY CHUNK");
                ReplayChunk chunk = new ReplayChunk(chunkID, new ReplayChunk.ChunkData(this.registry));
                chunk.addPlayerCodec(player.getUniqueId(), ticks, codec);
                chunks.put(chunkID, chunk);
            }
        }
    }

    public void stop() {
        stopped = System.currentTimeMillis() / 1000;
        stop = true;
        if (task != null) {
            task.cancel();
        }
        registry.listeningForCodecs(false);
        registry.unregisterCodecListener(codecListener);
    }
}
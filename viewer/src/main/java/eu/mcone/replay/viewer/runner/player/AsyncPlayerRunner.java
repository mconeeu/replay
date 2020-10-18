package eu.mcone.replay.viewer.runner.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.npc.entity.PlayerNpc;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.replay.core.api.chunk.ReplayChunk;
import eu.mcone.replay.core.api.player.ReplayPlayer;
import eu.mcone.replay.core.packets.player.block.BlockBreakEventCodec;
import eu.mcone.replay.core.packets.player.block.BlockPlaceEventCodec;
import eu.mcone.replay.viewer.ReplayViewer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.runner.PlayerRunner;
import eu.mcone.replay.viewer.api.runner.ReplayRunner;
import eu.mcone.replay.viewer.api.runner.ReplaySpeed;
import eu.mcone.replay.viewer.api.utils.SkipUnit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncPlayerRunner extends ReplayRunner implements PlayerRunner, eu.mcone.replay.viewer.api.runner.AsyncPlayerRunner {

    @Getter
    private final ReplayPlayer player;
    @Setter
    @Getter
    private ReplaySpeed speed = ReplaySpeed._1X;

    @Setter
    @Getter
    private boolean breaking = false;
    @Setter
    @Getter
    private boolean playing;
    @Getter
    @Setter
    private boolean forward;

    public AsyncPlayerRunner(ReplayPlayer player, ReplayContainer container) {
        super(container, ReplayViewer.getInstance().getReplayManager().getCodecRegistry());

        this.player = player;
        currentTick = new AtomicInteger();
    }

    @SuppressWarnings("rawtypes")
    public void execute() {
        ReplayChunk chunk = getContainer().getReplayChunkHandler().getChunk(currentTick.get());
        Map<Integer, List<Codec<?, ?>>> codecs = chunk.getPlayerCodecs(player.getUuid());

        int repeat;
        int skipped = 0;
        if (getSpeed() != null && skipped == getSpeed().getWait()) {
            repeat = (getSpeed().isAdd() ? 2 : 0);
            skipped = 0;
        } else {
            repeat = 1;
        }

        for (int i = 0; i < repeat; i++) {
            if (breaking) {
                player.getNpc().sendAnimation(NpcAnimation.SWING_ARM, getContainer().getViewers().toArray(new Player[0]));
            }

            if (codecs.containsKey(currentTick.get())) {
                for (Codec codec : codecs.get(currentTick.get())) {
                    encodeCodec(codec);
                }

                skipped++;
            }
        }
    }

    public void stop() {
        playing = false;
    }

    @Override
    public void restart() {
        currentTick.set(0);
        playing = true;
        play();
    }

    @Override
    public void skip(SkipUnit unit, int amount) {
        if (isPlaying()) {
            if (amount != 0) {
                int converted = convertToTicks(unit, amount);
                boolean isNegative = amount < 0;
                int lastTick = (isNegative ? currentTick.get() - converted : currentTick.get() + converted);

                if (lastTick < getContainer().getReplay().getLastTick() && lastTick > 0) {
                    getContainer().addIdling(this);
                    int currentChunk = currentTick.get() / getContainer().getReplayChunkHandler().getChunkLength();

                    ReplayChunk chunk;
                    Map<Integer, List<Codec<?, ?>>> codecs = null;

                    int tick = currentTick.get();
                    while ((isNegative ? tick > 0 : tick != getContainer().getReplay().getLastTick()) && tick < lastTick) {
                        int newChunk = tick / getContainer().getReplayChunkHandler().getChunkLength();
                        if (newChunk > currentChunk || codecs == null) {
                            chunk = getContainer().getReplayChunkHandler().getChunkByID(newChunk);
                            codecs = chunk.getPlayerCodecs(player.getUuid());
                        }

                        if (codecs.containsKey(tick)) {
                            for (Codec<?, ?> codec : codecs.get(tick)) {
                                if (codec instanceof BlockBreakEventCodec
                                        || codec instanceof BlockPlaceEventCodec) {
                                    encodeCodec(codec);
                                }
                            }
                        }

                        if (isNegative) {
                            tick--;
                        } else {
                            tick++;
                        }
                    }

                    getContainer().removeIdling(this);
                    this.currentTick.set(tick);
                } else {
                    this.currentTick.set(0);
                }

                setIdling(false);
            }
        }
    }

    private void encodeCodec(Codec codec) {
        Class<?> encoder = getCodecRegistry().getEncoderClass(codec.getEncoderID());
        if (encoder != null) {
            if (encoder.equals(PlayerNpc.class)) {
                codec.encode(player.getNpc());
            } else if (encoder.equals(PlayerRunner.class)) {
                codec.encode(this);
            } else if (encoder.equals(eu.mcone.replay.core.player.ReplayPlayer.class)) {
                codec.encode(player);
            }
        } else {
            ReplayViewer.getInstance().sendConsoleMessage("§cNo encoder for Codec " + codec.getClass().getSimpleName() + " found!");
        }
    }

    @Override
    public boolean isSync() {
        return false;
    }

    @Override
    public Collection<Player> getViewers() {
        return getContainer().getViewers();
    }
}

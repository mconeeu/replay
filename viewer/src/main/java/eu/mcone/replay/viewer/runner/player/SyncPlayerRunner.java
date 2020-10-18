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

public class SyncPlayerRunner extends ReplayRunner implements PlayerRunner {

    @Getter
    private final ReplayPlayer player;

    @Setter
    @Getter
    private boolean breaking = false;

    public SyncPlayerRunner(ReplayPlayer player, ReplayContainer container) {
        super(container, ReplayViewer.getInstance().getReplayManager().getCodecRegistry());
        this.player = player;
    }

    @SuppressWarnings("rawtypes")
    public void execute() {
        ReplayChunk chunk = getContainer().getReplayChunkHandler().getChunk(currentTick.get());
        Map<Integer, List<Codec<?, ?>>> codecs = chunk.getPlayerCodecs(player.getUuid());

        if (breaking) {
            player.getNpc().sendAnimation(NpcAnimation.SWING_ARM, getContainer().getViewers().toArray(new Player[0]));
        }

        if (codecs.containsKey(currentTick.get())) {
            for (Codec codec : codecs.get(currentTick.get())) {
                encodeCodec(codec);
            }
        }
    }

    public void stop() {
    }

    @Override
    public void restart() {
        currentTick.set(0);
    }

    @Override
    public void skip(SkipUnit unit, int amount) {
        if (getContainer().isPlaying()) {
            int converted = convertToTicks(unit, amount);

            System.out.println("DEBUG-1");
            if (converted != 0) {
                System.out.println("DEBUG-2");
                boolean isNegative = converted < 0;
                int lastTick = (isNegative ? currentTick.get() - converted : currentTick.get() + converted);

                System.out.println("LAST TICK: " + lastTick);
                System.out.println("LAST TICK 1: " + getContainer().getReplay().getLastTick());
                if (lastTick < getContainer().getReplay().getLastTick() && lastTick > 0) {
                    System.out.println("DEBUG-3");
                    getContainer().addIdling(this);

                    int currentChunk = this.currentTick.get() / getContainer().getReplayChunkHandler().getChunkLength();

                    ReplayChunk chunk;
                    Map<Integer, List<Codec<?, ?>>> codecs = null;

                    int tick = this.currentTick.get();
                    System.out.println("DEBUG-4");
                    while ((isNegative ? tick > 0 : tick != getContainer().getReplay().getLastTick()) && tick < lastTick) {
                        System.out.println("DEBUG-5");
                        int newChunk = tick / getContainer().getReplayChunkHandler().getChunkLength();
                        if (newChunk > currentChunk || codecs == null) {
                            System.out.println("DEBUG-6");
                            chunk = getContainer().getReplayChunkHandler().getChunkByID(newChunk);
                            codecs = chunk.getPlayerCodecs(player.getUuid());
                        }

                        System.out.println("DEBUG 7");
                        if (codecs.containsKey(tick)) {
                            System.out.println("DEBUG 8");
                            for (Codec<?, ?> codec : codecs.get(tick)) {
                                if (codec instanceof BlockBreakEventCodec
                                        || codec instanceof BlockPlaceEventCodec) {
                                    encodeCodec(codec);
                                }
                            }
                        }

                        System.out.println("DEBUG 9");

                        if (isNegative) {
                            System.out.println("DEBUG 10");
                            tick--;
                        } else {
                            System.out.println("DEBUG 11");
                            tick++;
                        }
                    }

                    System.out.println("DEBUG 12");
                    getContainer().removeIdling(this);
                    System.out.println("DEBUG 13");
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
    public boolean isForward() {
        return getContainer().isForward();
    }

    @Override
    public boolean isPlaying() {
        return getContainer().isPlaying();
    }

    @Override
    public ReplaySpeed getSpeed() {
        return getContainer().getSpeed();
    }

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public Collection<Player> getViewers() {
        return getContainer().getViewers();
    }
}

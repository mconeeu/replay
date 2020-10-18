package eu.mcone.replay.viewer.runner.server;

import eu.mcone.coresystem.api.bukkit.broadcast.Broadcast;
import eu.mcone.coresystem.api.bukkit.broadcast.BroadcastMessage;
import eu.mcone.gameapi.api.game.MessageWrapper;
import eu.mcone.replay.viewer.ReplayViewer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.runner.ReplayRunner;
import eu.mcone.replay.viewer.api.runner.ReplaySpeed;
import eu.mcone.replay.viewer.api.runner.ServerRunner;
import eu.mcone.replay.viewer.api.utils.SkipUnit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SyncServerRunner extends ReplayRunner implements ServerRunner {

    @Setter
    @Getter
    private ReplaySpeed speed = ReplaySpeed._1X;

    private final ReplayContainer container;

    public SyncServerRunner(final ReplayContainer container) {
        super(container, ReplayViewer.getInstance().getReplayManager().getCodecRegistry());
        this.container = container;
    }

    public void execute() {
        String sTick = String.valueOf(currentTick.get());
        if (getContainer().getReplay().getMessages().containsKey(sTick)) {
            for (MessageWrapper wrapper : getContainer().getReplay().getMessages().get(sTick)) {
                resendBroadcast(wrapper.getBroadcast(), getContainer().getViewers());
            }
        }
    }

    @Override
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
            if (converted != 0) {
                setIdling(true);
                boolean isNegative = converted < 0;
                int lastTick = (isNegative ? currentTick.get() - converted : currentTick.get() + converted);

                if (lastTick < getContainer().getReplay().getLastTick() && lastTick > 0) {
                    getContainer().addIdling(this);

                    int tick = currentTick.get();
                    String sTick;
                    while ((isNegative ? tick > 0 : tick != getContainer().getReplay().getLastTick()) && tick < lastTick) {
                        sTick = String.valueOf(tick);
                        if (getContainer().getReplay().getMessages().containsKey(sTick)) {
                            for (MessageWrapper wrapper : getContainer().getReplay().getMessages().get(sTick)) {
                                resendBroadcast(wrapper.getBroadcast(), getContainer().getViewers());
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

    @Override
    public boolean isSync() {
        return true;
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
    public Collection<Player> getViewers() {
        return container.getViewers();
    }

    static void resendBroadcast(Broadcast broadcast, Collection<Player> viewers) {
        BroadcastMessage message = broadcast.getMainMessage();

        for (Player viewer : viewers) {
            if (message.getTranslationReplacements() != null && message.getTranslationReplacements().length > 0) {
                ReplayViewer.getInstance().getMessenger().sendSimpleTransl(
                        viewer,
                        message.getMessageKey(),
                        message.getTranslationReplacements()
                );
            } else {
                ReplayViewer.getInstance().getMessenger().sendSimpleTransl(
                        viewer,
                        message.getMessageKey()
                );
            }
        }
    }

}

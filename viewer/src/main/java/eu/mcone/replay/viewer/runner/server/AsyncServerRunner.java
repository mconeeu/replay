package eu.mcone.replay.viewer.runner.server;

import eu.mcone.gameapi.api.game.MessageWrapper;
import eu.mcone.replay.viewer.ReplayViewer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.runner.ReplayRunner;
import eu.mcone.replay.viewer.api.runner.ReplaySpeed;
import eu.mcone.replay.viewer.api.utils.SkipUnit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Collection;

public class AsyncServerRunner extends ReplayRunner implements eu.mcone.replay.viewer.api.runner.AsyncServerRunner {

    @Setter
    @Getter
    private ReplaySpeed speed = null;
    @Getter
    @Setter
    private boolean playing;
    @Getter
    @Setter
    private boolean forward;

    public AsyncServerRunner(final ReplayContainer container) {
        super(container, ReplayViewer.getInstance().getReplayManager().getCodecRegistry());
    }

    public void execute() {
        int repeat;
        int skipped = 0;
        if (getSpeed() != null && skipped == getSpeed().getWait()) {
            repeat = (getSpeed().isAdd() ? 2 : 0);
            skipped = 0;
        } else {
            repeat = 1;
        }

        for (int i = 0; i < repeat; i++) {
            String sTick = String.valueOf(currentTick.get());
            if (getContainer().getReplay().getMessages().containsKey(sTick)) {
                for (MessageWrapper wrapper : getContainer().getReplay().getMessages().get(sTick)) {
                    SyncServerRunner.resendBroadcast(wrapper.getBroadcast(), getContainer().getViewers());

                    skipped++;
                }
            }
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {
        currentTick.set(0);
        playing = true;
        play();
    }

    @Override
    public void skip(SkipUnit unit, int amount) {
        if (playing) {
            int converted = convertToTicks(unit, amount);
            if (converted != 0) {
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
                                SyncServerRunner.resendBroadcast(wrapper.getBroadcast(), getContainer().getViewers());
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
        return false;
    }

    @Override
    public Collection<Player> getViewers() {
        return getContainer().getViewers();
    }
}

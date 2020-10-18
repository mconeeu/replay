package eu.mcone.replay.viewer.api.runner;

import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.replay.viewer.api.ReplayViewer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.utils.SkipUnit;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public abstract class ReplayRunner {

    private final ReplayContainer container;
    private final CodecRegistry codecRegistry;
    protected AtomicInteger currentTick;
    private BukkitTask task;
    @Setter
    private boolean idling;

    private int skipped = 0;

    public ReplayRunner(ReplayContainer container, CodecRegistry codecRegistry) {
        this.container = container;
        this.codecRegistry = codecRegistry;
        this.currentTick = new AtomicInteger(0);
    }

    public abstract boolean isSync();

    public void play() {
        if (task == null) {
            task = Bukkit.getScheduler().runTaskTimer(ReplayViewer.getInstance(), () -> {
                if (isPlaying()) {
                    if (currentTick.get() < container.getReplay().getLastTick()) {
                        int repeat;
                        if (getSpeed().getWait() != -1 && skipped >= getSpeed().getWait()) {
                            repeat = (getSpeed().isAdd() ? 2 : 0);
                            skipped = 0;
                        } else {
                            repeat = 1;
                        }

                        for (int i = 0; i < repeat; i++) {
                            execute();

                            if (isForward()) {
                                currentTick.getAndIncrement();
                            } else {
                                if (currentTick.get() >= 1) {
                                    currentTick.getAndDecrement();
                                } else {
                                    getContainer().forward(true);
                                    currentTick.getAndIncrement();
                                }
                            }

                            skipped++;
                        }
                    } else {
                        task.cancel();
                        stop();
                    }
                }
            }, 1, 1);
        }
    }

    private void test() {
        System.out.println("New Thread ----------------------------------------");
        System.out.println("Millis: " + System.currentTimeMillis());
        System.out.println("Before execution: " + MinecraftServer.getServer().recentTps[0]);

        Bukkit.getScheduler().runTaskLaterAsynchronously(ReplayViewer.getInstance(), () -> {
            if (isPlaying()) {
                if (currentTick.get() != container.getReplay().getLastTick()) {
                    execute();

                    if (isForward()) {
                        this.currentTick.getAndIncrement();
                    } else if (!isForward()) {
                        if (currentTick.get() >= 1) {
                            currentTick.getAndDecrement();
                        } else {
                            getContainer().forward(true);
                            currentTick.getAndIncrement();
                        }
                    }

                    System.out.println("After execution: " + MinecraftServer.getServer().recentTps[0]);
                    System.out.println("Millis: " + System.currentTimeMillis());
                    System.out.println("----------------------------------------");

                    test();
                } else {
                    task.cancel();
                    stop();
                }
            }
        }, 1);
    }

    public abstract void execute();

    public abstract void stop();

    public abstract void restart();

    public abstract void skip(SkipUnit unit, int amount);

    public abstract boolean isForward();

    public abstract boolean isPlaying();

    public abstract ReplaySpeed getSpeed();

    public abstract Collection<Player> getViewers();

    public static int convertToTicks(SkipUnit unit, int amount) {
        switch (unit) {
            case TICKS:
                return amount;
            case SECONDS:
                return amount * 20;
            case MINUTES:
                return amount * 20 * 60;
        }

        return 0;
    }
}

package eu.mcone.replay.viewer.api.runner;

import lombok.Getter;

@Getter
public enum ReplaySpeed {
    _0_5X(5, false, 2, "§70.5x"),
    _0_75X(6, false, 4, "§70.75x"),
    _1X(1, true, -1, "§71x"),

    _1_25X(2, true, 3, "§71.25x"),
    _1_5X(3, true, 2, "§71.50x"),
    _2X(4, true, 0, "§72x");

    private final int id;
    private final boolean add;
    private final int wait;
    private final String prefix;

    /**
     * @param id   ID
     * @param add  Should codecs be added?
     * @param wait Should codecs be skipped?
     */
    ReplaySpeed(int id, boolean add, int wait, String prefix) {
        this.id = id;
        this.add = add;
        this.wait = wait;
        this.prefix = prefix;
    }

    public static ReplaySpeed getWhereID(int id) {
        for (ReplaySpeed replaySpeed : values()) {
            if (replaySpeed.getId() == id) {
                return replaySpeed;
            }
        }

        return null;
    }

    public ReplaySpeed getNextSpeed() {
        if (id == 6) {
            return _1X;
        } else {
            return getWhereID(id + 1);
        }
    }
}

package eu.mcone.replay.core.api.util;

import java.util.Locale;

public class ReplayUtils {

    public static String getLength(float lastTick) {
        double seconds = lastTick / 20;
        if (seconds < 60) {
            return (int) seconds + " §7Sekunden";
        } else {
            double time = Double.parseDouble(String.format(Locale.ENGLISH, "%1.2f", seconds / 60));

            if (time >= 1) {
                return time + " §7Minute";
            } else {
                return time + " §7Minuten";
            }
        }
    }
}

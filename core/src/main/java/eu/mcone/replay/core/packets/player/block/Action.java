package eu.mcone.replay.core.packets.player.block;

import lombok.Getter;

@Getter
public enum Action {

    LEFT_CLICK_BLOCK((byte) 0),
    RIGHT_CLICK_BLOCK((byte) 1),
    LEFT_CLICK_AIR((byte) 2),
    RIGHT_CLICK_AIR((byte) 3),
    PHYSICAL((byte) 4);

    private final byte id;

    Action(byte id) {
        this.id = id;
    }

    public static Action fromNMS(org.bukkit.event.block.Action action) {
        return valueOf(action.toString());
    }

    public static Action getWhereID(byte id) {
        for (Action action : values()) {
            if (action.getId() == id) {
                return action;
            }
        }

        return null;
    }
}

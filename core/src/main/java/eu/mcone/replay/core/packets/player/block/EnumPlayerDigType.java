package eu.mcone.replay.core.packets.player.block;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;

@Getter
public enum EnumPlayerDigType {
    START_DESTROY_BLOCK((byte) 1),
    ABORT_DESTROY_BLOCK((byte) 2),
    STOP_DESTROY_BLOCK((byte) 3),
    DROP_ALL_ITEMS((byte) 4),
    DROP_ITEM((byte) 5),
    RELEASE_USE_ITEM((byte) 6);

    private final byte id;

    EnumPlayerDigType(byte id) {
        this.id = id;
    }

    public static EnumPlayerDigType fromNMS(PacketPlayInBlockDig.EnumPlayerDigType typ) {
        return valueOf(typ.toString());
    }

    public static EnumPlayerDigType getWhereID(byte id) {
        for (EnumPlayerDigType digType : values()) {
            if (digType.getId() == id) {
                return digType;
            }
        }

        return null;
    }
}

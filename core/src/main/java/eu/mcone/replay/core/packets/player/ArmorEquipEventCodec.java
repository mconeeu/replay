package eu.mcone.replay.core.packets.player;

import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.ItemStackTypeAdapterUtils;
import eu.mcone.coresystem.api.bukkit.event.armor.ArmorEquipEvent;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.npc.enums.EquipmentPosition;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ArmorEquipEventCodec extends Codec<ArmorEquipEvent, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private int material;
    private String enchantments;
    private byte slot;

    public ArmorEquipEventCodec() {
        super((byte) 16, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, ArmorEquipEvent armorEquipEvent) {
        if (armorEquipEvent.getNewArmorPiece() != null) {
            material = armorEquipEvent.getNewArmorPiece().getType().getId();
            enchantments = ItemStackTypeAdapterUtils.serializeEnchantments(armorEquipEvent.getNewArmorPiece().getEnchantments());
            slot = (byte) armorEquipEvent.getType().getSlot();
            return new Object[]{armorEquipEvent.getPlayer()};
        } else {
            return null;
        }
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.getMaterial(material), 1).enchantments(ItemStackTypeAdapterUtils.getEnchantments(enchantments));
        runner.getPlayer().getNpc().setEquipment(EquipmentPosition.getPosition(slot), itemBuilder.create());
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeInt(material);
        out.writeUTF(enchantments);
        out.writeByte(slot);
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        material = in.readInt();
        enchantments = in.readUTF();
        slot = in.readByte();
    }
}

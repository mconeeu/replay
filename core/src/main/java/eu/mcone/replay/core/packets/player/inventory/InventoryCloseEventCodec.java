package eu.mcone.replay.core.packets.player.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.codec.Codec;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.ItemStackTypeAdapterUtils;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.replay.core.api.runner.SimplePlayerRunner;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

@Getter
public class InventoryCloseEventCodec extends Codec<InventoryCloseEvent, SimplePlayerRunner> {

    public static final byte CODEC_VERSION = 1;

    private int material;
    private String enchantments;
    private HashMap<Integer, ItemStack> content;

    public InventoryCloseEventCodec() {
        super((byte) 17, (byte) 3);
    }

    @Override
    public Object[] decode(Player player, InventoryCloseEvent closeEvent) {
        Player invOwner = (Player) closeEvent.getPlayer();
        //Item in hand
        ItemStack itemStack = closeEvent.getPlayer().getItemInHand();
        if (itemStack != null) {
            this.material = itemStack.getType().getId();
            this.enchantments = ItemStackTypeAdapterUtils.serializeEnchantments(itemStack.getEnchantments());
        }

        //Inventory
        content = new HashMap<>();

        for (int i = 0; i < invOwner.getInventory().getSize(); i++) {
            ItemStack item = invOwner.getInventory().getItem(i);

            if (item != null) {
                content.put(i, item);
            }
        }

        return new Object[]{closeEvent.getPlayer()};
    }

    @Override
    public void encode(SimplePlayerRunner runner) {
        runner.getPlayer().getNpc().setItemInHand(getItem());
        runner.getPlayer().setInventoryItems(content);
    }

    @Override
    protected void onWriteObject(DataOutputStream out) throws IOException {
        out.writeInt(material);
        out.writeUTF(enchantments);
        out.writeUTF(CoreSystem.getInstance().getGson().toJson(content));
    }

    @Override
    protected void onReadObject(DataInputStream in) throws IOException {
        material = in.readInt();
        enchantments = in.readUTF();
        content = (HashMap<Integer, ItemStack>) CoreSystem.getInstance().getGson().fromJson(in.readUTF(), HashMap.class);
    }

    public ItemStack getItem() {
        return new ItemBuilder(Material.getMaterial(material), 1).enchantments(ItemStackTypeAdapterUtils.getEnchantments(enchantments)).create();
    }
}

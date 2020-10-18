package eu.mcone.replay.sever.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.bukkit.util.CoreTitle;
import eu.mcone.replay.sever.ReplayServer;
import eu.mcone.replay.viewer.api.ReplayViewer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import eu.mcone.replay.viewer.api.utils.SkipUnit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    public static final int SKIP_COOLDOWN = 5;

    public static final ItemStack SPAWN = new ItemBuilder(Material.FIREBALL, 1).displayName("§e§lSpawn").create();

    //Slot 1
    public static final ItemStack REPLAY_TELEPORT = new ItemBuilder(Material.COMPASS, 1).displayName("§7Teleporter").create();
    //Slot 2
    public static final ItemStack REPLAY_INFORMATION = new ItemBuilder(Material.REDSTONE_COMPARATOR, 1).displayName("§eReplay").create();

    //Slot 4
    public static final ItemStack REPLAY_SKIP_FORWARD = Skull.fromUrl("http://textures.minecraft.net/texture/2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f").toItemBuilder().displayName("§7Überspringen §8(§e30 §7Sekunden§8)").create();
    //Slot 5
    public static final ItemStack REPLAY_PAUSE = Skull.fromUrl("http://textures.minecraft.net/texture/abae89e92ac362635ba3e9fb7c12b7ddd9b38adb11df8aa1aff3e51ac428a4").toItemBuilder().displayName("§7Pausieren").create();
    //Slot 5
    public static final ItemStack REPLAY_START = Skull.fromUrl("http://textures.minecraft.net/texture/6527ebae9f153154a7ed49c88c02b5a9a9ca7cb1618d9914a3d9df8ccb3c84").toItemBuilder().displayName("§7Vortfahren").create();
    //Slot 6
    public static final ItemStack REPLAY_SKIP_BACKWARD = Skull.fromUrl("http://textures.minecraft.net/texture/8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f").toItemBuilder().displayName("§7Zurückspringen §8(§e30 §7Sekunden§8)").create();

    //Slot 7
    public static final ItemStack REPLAY_FORWARD = Skull.fromUrl("http://textures.minecraft.net/texture/19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").toItemBuilder().displayName("§7Vorwährts§8/§7§lRückwährts").create();
    //Slot 7
    public static final ItemStack REPLAY_BACKWARD = Skull.fromUrl("http://textures.minecraft.net/texture/bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9").toItemBuilder().displayName("§7§lVorwährts§8/§7Rückwährts").create();

    //Slot 8
    public static final ItemStack REPLAY_SPEED_INCREASE = Skull.fromUrl("http://textures.minecraft.net/texture/3edd20be93520949e6ce789dc4f43efaeb28c717ee6bfcbbe02780142f716").toItemBuilder().displayName("§7Geschwindigkeit").create();

    public PlayerInteractListener() {
        CoreSystem.getInstance().getCooldownSystem().setCustomCooldownFor(PlayerInteractListener.class, SKIP_COOLDOWN);
    }

    @EventHandler
    public void on(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack clickedItem = e.getItem();

        if (clickedItem != null) {
            if (clickedItem == SPAWN) {
                if (CoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), player.getUniqueId())) {
                    player.teleport(ReplayServer.getInstance().getReplayWorld().getLocation("spawn"));
                    ReplayServer.getInstance().getMessenger().send(player, "§7Du wurdest zum Spawn teleportiert");
                    CoreSystem.getInstance().getCooldownSystem().addPlayer(player.getUniqueId(), this.getClass());
                }
            } else {
                ReplayContainer container = ReplayViewer.getInstance().getReplayViewManager().getContainer(player);
                if (container != null) {
                    CoreTitle title = null;

                    if (container.getViewers().contains(player)) {
                        if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(REPLAY_TELEPORT.getItemMeta().getDisplayName())) {
                            container.openSpectatorInventory(player);
                        } else if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(REPLAY_INFORMATION.getItemMeta().getDisplayName())) {
                            container.getReplay().openInformationInventory(player);
                        }

                        if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(REPLAY_PAUSE.getItemMeta().getDisplayName())) {
                            if (container.isPlaying()) {
                                container.playing(false);
                                title = CoreSystem.getInstance().createTitle().title("§eReplay").subTitle("§8» §cPause");
                                ReplayServer.getInstance().getMessenger().send(player, "§aDas Replay pausiert nun...");
                            } else {
                                ReplayServer.getInstance().getMessenger().send(player, "§cDas Replay pausiert bereits!");
                            }
                        } else if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(REPLAY_START.getItemMeta().getDisplayName())) {
                            if (container.isPlaying()) {
                                ReplayServer.getInstance().getMessenger().send(player, "§cDas Replay läuft bereits!");
                            } else {
                                if (container.getTick() > 0) {
                                    container.playing(true);
                                } else {
                                    container.play();
                                }

                                title = CoreSystem.getInstance().createTitle().title("§eReplay").subTitle("§8» §aWeiter");
                                ReplayServer.getInstance().getMessenger().send(player, "§aDu hast das Replay gestartet!");
                            }
                        }

                        if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(REPLAY_SKIP_FORWARD.getItemMeta().getDisplayName())) {
                            if (checkSkip(player)) {
                                container.skip(SkipUnit.SECONDS, 15);
                                title = CoreSystem.getInstance().createTitle().title("§715 Sekunden").subTitle("§8» §aÜbersprungen");
                                ReplayServer.getInstance().getMessenger().send(player, "§7Du hast 15 Sekunden übersprungen");
                            } else {
                                e.setCancelled(true);
                            }
                        } else if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(REPLAY_SKIP_BACKWARD.getItemMeta().getDisplayName())) {
                            if (checkSkip(player)) {
                                container.skip(SkipUnit.SECONDS, -15);
                                title = CoreSystem.getInstance().createTitle().title("§715 Sekunden").subTitle("§8» §cZurück gesprungen");
                                ReplayServer.getInstance().getMessenger().send(player, "§7Du bist 15 Sekunden zurück gesprungen");
                            } else {
                                e.setCancelled(true);
                            }
                        }

                        if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(REPLAY_FORWARD.getItemMeta().getDisplayName())) {
                            if (!container.isForward()) {
                                container.forward(true);
                                title = CoreSystem.getInstance().createTitle().title("§eReplay").subTitle("§8» §aVorwährts");
                                ReplayServer.getInstance().getMessenger().send(player, "§aDas Replay läuft nun wieder vorwährst...");
                            } else {
                                ReplayServer.getInstance().getMessenger().send(player, "§cDas Replay läuft bereits vorwährts!");
                            }
                        } else if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(REPLAY_BACKWARD.getItemMeta().getDisplayName())) {
                            if (container.isForward()) {
                                container.forward(false);
                                title = CoreSystem.getInstance().createTitle().title("§eReplay").subTitle("§8» §cRückwährts");
                                ReplayServer.getInstance().getMessenger().send(player, "§aDas Replay läuft nun rückwährst...");
                            } else {
                                ReplayServer.getInstance().getMessenger().send(player, "§cDas Replay läuft bereits rückwährst!");
                            }
                        }

                        if (clickedItem.getItemMeta().getDisplayName().equalsIgnoreCase(REPLAY_SPEED_INCREASE.getItemMeta().getDisplayName())) {
                            container.nextSpeed();
                            title = CoreSystem.getInstance().createTitle().title("§aGeschwindigkeit").subTitle("§8» " + container.getSpeed().getPrefix());
                            for (Player viewer : container.getViewers()) {
                                title.send(viewer);
                            }
                        }

                        if (title != null) {
                            for (Player viewer : container.getViewers()) {
                                title.send(viewer);
                            }
                        }

                        setItem(container);
                    }
                }
            }
        }
    }

    private boolean checkSkip(Player player) {
        if (SKIP_COOLDOWN > 0
                && !CoreSystem.getInstance().getCooldownSystem().addAndCheck(getClass(), player.getUniqueId())
                && !player.hasPermission("system.bukkit.chat.cooldown.bypass")) {
            CoreSystem.getInstance().getMessenger().send(player, "Bitte warte " + SKIP_COOLDOWN + " Sekunden bevor du wieder skippen kannst!");
            return false;
        }

        return true;
    }

    public static void setItem(eu.mcone.replay.viewer.api.container.ReplayContainer container) {
        for (Player viewer : container.getViewers()) {
            viewer.getInventory().setItem(0, REPLAY_TELEPORT);
            viewer.getInventory().setItem(1, REPLAY_INFORMATION);

            viewer.getInventory().setItem(3, REPLAY_SKIP_BACKWARD);
            viewer.getInventory().setItem(4, (container.isPlaying() ? REPLAY_PAUSE : REPLAY_START));
            viewer.getInventory().setItem(5, REPLAY_SKIP_FORWARD);

            viewer.getInventory().setItem(7, (container.isForward() ? REPLAY_BACKWARD : REPLAY_FORWARD));
            viewer.getInventory().setItem(8, REPLAY_SPEED_INCREASE);
        }
    }
}

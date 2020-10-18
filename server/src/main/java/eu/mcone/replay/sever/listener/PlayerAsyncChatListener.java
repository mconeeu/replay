package eu.mcone.replay.sever.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.replay.sever.ReplayServer;
import eu.mcone.replay.viewer.api.ReplayViewer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerAsyncChatListener implements Listener {

    @EventHandler
    public void on(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);

        if (!ReplayServer.getInstance().getViewers().contains(p)) {
            ReplayContainer container = ReplayViewer.getInstance().getReplayViewManager().getContainer(p);
            if (container != null) {

                for (Player watcher : container.getViewers()) {
                    watcher.sendMessage("§8[§e§lS§8] " + (cp.isNicked() ? Group.SPIELER.getPrefix() : cp.getMainGroup().getPrefix()) + CoreSystem.getInstance().getTranslationManager().get("system.bukkit.chat").replaceAll("%Player%", p.getName()) + " §7" + e.getMessage());
                }

                e.setCancelled(true);
            }
        } else {
            if (!cp.isVanished()) {
                String playerMessage = e.getMessage();
                for (Player receiver : ReplayServer.getInstance().getViewers()) {
                    if (receiver != p) {
                        String targetMessage;

                        if (e.getMessage().contains(receiver.getName())) {
                            if (e.getMessage().contains("@" + receiver.getName())) {
                                targetMessage = e.getMessage().replaceAll("@" + receiver.getName(), "§b@" + receiver.getName() + "§7");
                                playerMessage = playerMessage.replaceAll("@" + receiver.getName(), ChatColor.AQUA + "@" + receiver.getName() + ChatColor.GRAY);
                            } else {
                                targetMessage = e.getMessage().replaceAll(receiver.getName(), "§b@" + receiver.getName() + "§7");
                                playerMessage = playerMessage.replaceAll(receiver.getName(), "§b@" + receiver.getName() + "§7");
                            }

                            e.getRecipients().remove(receiver);
                            receiver.sendMessage((cp.isNicked() ? Group.SPIELER.getPrefix() : cp.getMainGroup().getPrefix()) + CoreSystem.getInstance().getTranslationManager().get("system.bukkit.chat").replaceAll("%Player%", p.getName()) + targetMessage);
                            receiver.playSound(receiver.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                        } else {
                            receiver.sendMessage((cp.isNicked() ? Group.SPIELER.getPrefix() : cp.getMainGroup().getPrefix()) + CoreSystem.getInstance().getTranslationManager().get("system.bukkit.chat").replaceAll("%Player%", p.getName()) + " §7" + e.getMessage());
                        }
                    }
                }

                e.getRecipients().remove(p);
                p.sendMessage((cp.isNicked() ? Group.SPIELER.getPrefix() : cp.getMainGroup().getPrefix()) + CoreSystem.getInstance().getTranslationManager().get("system.bukkit.chat").replaceAll("%Player%", p.getName()) + playerMessage);
                e.setCancelled(true);
            } else {
                e.setCancelled(true);
                CoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze §c/vc <message>§4 um eine Chatnachricht zu schreiben während du im Vanish-Modus bist!");
            }
        }
    }
}

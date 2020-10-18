package eu.mcone.replay.sever.command;

import com.mongodb.client.FindIterable;
import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.replay.viewer.api.Replay;
import eu.mcone.replay.viewer.api.ReplayViewer;
import eu.mcone.replay.viewer.api.container.ReplayContainer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReplayCMD extends CorePlayerCommand {

    public ReplayCMD() {
        super("replay");
    }

    @Override
    public boolean onPlayerCommand(Player p, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("leave")) {
                ReplayContainer container = ReplayViewer.getInstance().getReplayViewManager().getContainer(p);

                if (container != null) {
                    container.removeViewers(p);
                    ReplayViewer.getInstance().getMessenger().send(p, "§7Du hast das Replay §cverlassen!");
                } else {
                    ReplayViewer.getInstance().getMessenger().send(p, "§cDu bist aktuell in keinem Replay!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                FindIterable<Replay> replays = ReplayViewer.getInstance().getReplayManager().getReplaysForPlayer(p.getUniqueId(), 1, 15, Replay.class);

                if (ReplayViewer.getInstance().getReplayManager().countReplaysForPlayer(p) < 0) {
                    ReplayViewer.getInstance().getMessenger().send(p, "§cEs existieren keine Replays in denen du enthalten bisst!");
                } else {
                    ReplayViewer.getInstance().getMessenger().send(p, "§7Du kommst in folgenden Replays vor:");
                    for (Replay replay : replays) {
                        ComponentBuilder componentBuilder = new ComponentBuilder("§eReplay: §f" + replay.getID() + " §eVom: §f" + new SimpleDateFormat("dd.MM.yyyy").format(new Date(replay.getGameHistory().getStarted() * 1000)))
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§lReplay starten \n§7§oLinksklick zum starten").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/replay join " + replay.getID()));
                        p.spigot().sendMessage(componentBuilder.create());
                    }
                }

                return true;
            }

            return false;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete")) {
                if (p.hasPermission("replay.delete")) {
                    String ID = args[1];

                    if (ID != null && !ID.isEmpty()) {
                        boolean succeed = ReplayViewer.getInstance().getReplayManager().deleteReplay(ID);

                        if (succeed) {
                            ReplayViewer.getInstance().getMessenger().send(p, "§aDie replay session mit der ID §f" + ID + " §awurde erfolgreich gelöscht!");
                        } else {
                            ReplayViewer.getInstance().getMessenger().send(p, "§cDie replay session mit der ID §f" + ID + " §ckonnte nicht §4gefunden §cwerden!");
                        }

                    } else {
                        ReplayViewer.getInstance().getMessenger().send(p, "§cBitte gibt eine ReplayID an!");
                    }

                    return true;
                } else {
                    ReplayViewer.getInstance().getMessenger().send(p, "§cDu hast keine Berechtigung für diesen Befehl!");
                }
            } else if (args[0].equalsIgnoreCase("join")) {
                if (args[1] != null) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target != null) {
                        ReplayContainer currently = ReplayViewer.getInstance().getReplayViewManager().getContainer(p);
                        ReplayContainer container = ReplayViewer.getInstance().getReplayViewManager().getContainer(target);

                        if (container != null) {
                            if (currently != null) {
                                container.removeViewers(p);
                            }

                            container.addViewers(p);
                            ReplayViewer.getInstance().getMessenger().send(p, "§7Du bist dem Replay beigetreten");
                        } else {
                            ReplayViewer.getInstance().getMessenger().send(p, "§cDer Spieler §f" + target.getName() + " §cschaut aktuell kein Replay!");
                        }
                    } else {
                        ReplayViewer.getInstance().getMessenger().send(p, "§cDer Spieler " + args[0] + " konnte nicht gefunden werden!");
                    }
                } else {
                    ReplayViewer.getInstance().getMessenger().send(p, "§cBitte gib einen Spielernamen an!");
                }

                return true;
            } else if (args[0].equalsIgnoreCase("invite")) {
                String name = args[1];
                if (name != null) {
                    Player target = Bukkit.getPlayer(name);

                    if (target != null) {
                        if (target != p) {
                            ReplayContainer container = ReplayViewer.getInstance().getReplayViewManager().getContainer(p);

                            if (container != null) {
                                container.invite(p, target);
                            } else {
                                ReplayViewer.getInstance().getMessenger().send(p, "§cDu siehst dir momentan kein Replay an!");
                            }
                        } else {
                            ReplayViewer.getInstance().getMessenger().send(p, "§cDu kannst dich nicht selbst einladen!");
                        }
                    } else {
                        ReplayViewer.getInstance().getMessenger().send(p, "§7Der Spieler §f" + name + " §7konnte nicht gefunden werden!");
                    }

                    return true;
                } else {
                    ReplayViewer.getInstance().getMessenger().send(p, "§cBitte verwende: /replay invite <spieler>");
                }
            }
        }

        if (p.hasPermission("replay.remove")) {
            ReplayViewer.getInstance().getMessenger().send(p, "§4Bitte benutze: " +
                    "\n§c/replay delete <name> §4oder" +
                    "\n§c/replay leave §4oder " +
                    "\n§c/replay list §4oder " +
                    "\n§c/replay join <session> §4oder " +
                    "\n§c/replay invite <spieler>"
            );
        } else {
            ReplayViewer.getInstance().getMessenger().send(p, "§4Bitte benutze: " +
                    "\n§c/replay leave §4oder " +
                    "\n§c/replay list §4oder " +
                    "\n§c/replay join <session> §4oder " +
                    "\n§c/replay invite <spieler>"
            );
        }

        return false;
    }
}

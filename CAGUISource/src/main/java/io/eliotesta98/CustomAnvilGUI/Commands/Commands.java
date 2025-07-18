package io.eliotesta98.CustomAnvilGUI.Commands;

import com.HeroxWar.HeroxCore.MessageGesture;
import com.HeroxWar.HeroxCore.ReloadGesture;
import io.eliotesta98.CustomAnvilGUI.Core.Main;
import io.eliotesta98.CustomAnvilGUI.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    private final String errorNoPlayer = Main.instance.getConfigGestion().getMessages().get("Errors.NoPlayer");
    private final String commandFooter = Main.instance.getConfigGestion().getMessages().get("Commands.Footer");
    private final String errorCommandNotFound = Main.instance.getConfigGestion().getMessages().get("Errors.CommandNotFound");
    private final String errorInsufficientPermission = Main.instance.getConfigGestion().getMessages().get("Errors.InsufficientPermission");
    private final String commandHelpHelp = Main.instance.getConfigGestion().getMessages().get("Commands.Help");
    private final String commandReloadHelp = Main.instance.getConfigGestion().getMessages().get("Commands.Reload");
    private final boolean debugCommands = Main.instance.getConfigGestion().getDebug().get("Commands");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DebugUtils debug = new DebugUtils();
        long tempo = System.currentTimeMillis();
        if (!(sender instanceof Player)) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                if (!command.getName().equalsIgnoreCase("customanvilgui")) {// comando se esiste
                    MessageGesture.sendMessage(sender,errorCommandNotFound);
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                    return;
                }
                // no args
                if (args.length == 0) {
                    String finale = "&e&lCustomAnvilGUI &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    finale = finale + commandReloadHelp + "\n";
                    finale = finale + commandFooter;
                    MessageGesture.sendMessage(sender,finale);
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
                // help
                else if (args[0].equalsIgnoreCase("help")) {
                    if (args.length != 1) {
                        MessageGesture.sendMessage(sender,commandHelpHelp);
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    String finale = "&e&lCustomAnvilGUI &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    finale = finale + commandReloadHelp + "\n";
                    finale = finale + "\n";
                    finale = finale + commandFooter;
                    MessageGesture.sendMessage(sender,finale);
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
                // reload
                else if (args[0].equalsIgnoreCase("reload")) {
                    if (args.length != 1) {
                        MessageGesture.sendMessage(sender,commandReloadHelp);
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        MessageGesture.sendMessage(sender,"&6Reloading...");
                        ReloadGesture.reload(Main.instance.getName());
                        MessageGesture.sendMessage(sender,"&aReloaded!");
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    });
                }
                // args incorrect
                else {
                    String finale = "&e&lCustomAnvilGUI &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    finale = finale + commandReloadHelp + "\n";
                    finale = finale + "\n";
                    finale = finale + commandFooter;
                    MessageGesture.sendMessage(sender,finale);
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
            });
        } else {
            final Player p = (Player) sender;
            if (!command.getName().equalsIgnoreCase("customanvilgui")) {// comando se esiste
                MessageGesture.sendMessage(sender,errorCommandNotFound);
                if (debugCommands) {
                    debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                    debug.debug("Commands");
                }
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                // no args
                if (args.length == 0) {
                    String finale = "&e&lCustomAnvilGUI &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    if (p.hasPermission("cagui.command.reload")) {
                        finale = finale + commandReloadHelp + "\n";
                    }
                    finale = finale + "\n";
                    finale = finale + commandFooter;
                    MessageGesture.sendMessage(sender,finale);
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
                // help
                else if (args[0].equalsIgnoreCase("help")) {
                    if (!p.hasPermission("cagui.command.help")) {
                        MessageGesture.sendMessage(sender,errorInsufficientPermission);
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length != 1) {
                        MessageGesture.sendMessage(sender,commandHelpHelp);
                    } else {
                        String finale = "&e&lCustomAnvilGUI &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                        if (p.hasPermission("cagui.command.reload")) {
                            finale = finale + commandReloadHelp + "\n";
                        }
                        finale = finale + "\n";
                        finale = finale + commandFooter;
                        MessageGesture.sendMessage(sender,finale);
                    }
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
                // reload
                else if (args[0].equalsIgnoreCase("reload")) {
                    // controllo se ha il permesso
                    if (!p.hasPermission("cagui.command.reload")) {
                        MessageGesture.sendMessage(sender,errorInsufficientPermission);
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    // deve scrivere p reload
                    if (args.length != 1) {
                        MessageGesture.sendMessage(sender,commandReloadHelp);
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        MessageGesture.sendMessage(sender,"&6Reloading...");
                        ReloadGesture.reload(Main.instance.getName());
                        MessageGesture.sendMessage(sender,"&aReloaded!");
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    });
                }
                // args incorrect
                else {
                    String finale = "&e&lCustomAnvilGUI &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    if (p.hasPermission("cagui.command.reload")) {
                        finale = finale + commandReloadHelp + "\n";
                    }
                    finale = finale + "\n";
                    finale = finale + commandFooter;
                    MessageGesture.sendMessage(sender,finale);
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
            });
        }
        return false;
    }
}

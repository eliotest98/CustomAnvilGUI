package io.eliotesta98.AnvilPlus.Commands;

import io.eliotesta98.AnvilPlus.Core.Main;
import io.eliotesta98.AnvilPlus.Utils.ColorUtils;
import io.eliotesta98.AnvilPlus.Utils.DebugUtils;
import io.eliotesta98.AnvilPlus.Utils.ReloadUtils;
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
                if (!command.getName().equalsIgnoreCase("anvilplus")) {// comando se esiste
                    sender.sendMessage(ColorUtils.applyColor(errorCommandNotFound));
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                    return;
                }
                // no args
                if (args.length == 0) {
                    String finale = "&e&lCustomGuiForAnvil &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    finale = finale + commandHelpHelp + "\n";
                    finale = finale + commandReloadHelp + "\n";
                    finale = finale + commandFooter;
                    sender.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
                // help
                else if (args[0].equalsIgnoreCase("help")) {
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandHelpHelp));
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    String finale = "&e&lCustomGuiForAnvil &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    finale = finale + commandHelpHelp + "\n";
                    finale = finale + commandReloadHelp + "\n";
                    finale = finale + "\n";
                    finale = finale + commandFooter;
                    sender.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
                // reload
                else if (args[0].equalsIgnoreCase("reload")) {
                    if (args.length != 1) {
                        sender.sendMessage(ColorUtils.applyColor(commandReloadHelp));
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        sender.sendMessage(ColorUtils.applyColor("&6Reloading..."));
                        ReloadUtils.reload();
                        sender.sendMessage(ColorUtils.applyColor("&aReloaded!"));
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    });
                }
                // args incorrect
                else {
                    String finale = "&e&lAnvilPlus &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    finale = finale + commandHelpHelp + "\n";
                    finale = finale + commandReloadHelp + "\n";
                    finale = finale + "\n";
                    finale = finale + commandFooter;
                    sender.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
            });
        } else {
            final Player p = (Player) sender;
            if (!command.getName().equalsIgnoreCase("anvilplus")) {// comando se esiste
                p.sendMessage(ColorUtils.applyColor(errorCommandNotFound));
                if (debugCommands) {
                    debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                    debug.debug("Commands");
                }
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
                // no args
                if (args.length == 0) {
                    String finale = "&e&lAnvilPlus &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    if (p.hasPermission("cgfa.command.help")) {
                        finale = finale + commandHelpHelp + "\n";
                    }
                    if (p.hasPermission("cgfa.command.reload")) {
                        finale = finale + commandReloadHelp + "\n";
                    }
                    finale = finale + "\n";
                    finale = finale + commandFooter;
                    p.sendMessage(ColorUtils.applyColor(finale));
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
                // help
                else if (args[0].equalsIgnoreCase("help")) {
                    if (!p.hasPermission("ap.command.help")) {
                        p.sendMessage(ColorUtils.applyColor(errorInsufficientPermission));
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    if (args.length != 1) {
                        p.sendMessage(ColorUtils.applyColor(commandHelpHelp));
                    } else {
                        String finale = "&e&lAnvilPlus &7● Version " + Main.instance.getDescription().getVersion()
                                + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                        if (p.hasPermission("cgfa.command.help")) {
                            finale = finale + commandHelpHelp + "\n";
                        }
                        if (p.hasPermission("cgfa.command.reload")) {
                            finale = finale + commandReloadHelp + "\n";
                        }
                        finale = finale + "\n";
                        finale = finale + commandFooter;
                        p.sendMessage(ColorUtils.applyColor(finale));
                    }
                    if (debugCommands) {
                        debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Commands");
                    }
                }
                // reload
                else if (args[0].equalsIgnoreCase("reload")) {
                    // controllo se ha il permesso
                    if (!p.hasPermission("ap.command.reload")) {
                        p.sendMessage(ColorUtils.applyColor(errorInsufficientPermission));
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    // deve scrivere p reload
                    if (args.length != 1) {
                        p.sendMessage(ColorUtils.applyColor(commandReloadHelp));
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        sender.sendMessage(ColorUtils.applyColor("&6Reloading..."));
                        ReloadUtils.reload();
                        sender.sendMessage(ColorUtils.applyColor("&aReloaded!"));
                        if (debugCommands) {
                            debug.addLine("Commands execution time= " + (System.currentTimeMillis() - tempo));
                            debug.debug("Commands");
                        }
                    });
                }
                // args incorrect
                else {
                    String finale = "&e&lAnvilPlus &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    if (p.hasPermission("cgfa.command.help")) {
                        finale = finale + commandHelpHelp + "\n";
                    }
                    if (p.hasPermission("cgfa.command.reload")) {
                        finale = finale + commandReloadHelp + "\n";
                    }
                    finale = finale + "\n";
                    finale = finale + commandFooter;
                    p.sendMessage(ColorUtils.applyColor(finale));
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

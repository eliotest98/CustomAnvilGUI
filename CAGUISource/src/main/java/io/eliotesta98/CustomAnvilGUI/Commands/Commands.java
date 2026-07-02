package io.eliotesta98.CustomAnvilGUI.Commands;

import com.HeroxWar.HeroxCore.ReloadGesture;
import io.eliotesta98.CustomAnvilGUI.Core.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

    private final String errorNoPlayer = Main.instance.getConfigGestion().getMessages().get("Errors.NoPlayer");
    private final String commandFooter = Main.instance.getConfigGestion().getMessages().get("Commands.Footer");
    private final String errorCommandNotFound = Main.instance.getConfigGestion().getMessages().get("Errors.CommandNotFound");
    private final String errorInsufficientPermission = Main.instance.getConfigGestion().getMessages().get("Errors.InsufficientPermission");
    private final String commandHelpHelp = Main.instance.getConfigGestion().getMessages().get("Commands.Help");
    private final String commandReloadHelp = Main.instance.getConfigGestion().getMessages().get("Commands.Reload");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (!command.getName().equalsIgnoreCase("customanvilgui")) {// comando se esiste
                Main.messageGesturePaper.sendMessage(sender, errorCommandNotFound);
                return;
            }
            if (args.length == 0) {
                String finale = "&e&lCustomAnvilGUI &7● Version " + Main.instance.getDescription().getVersion()
                        + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                finale = finale + commandHelpHelp + "\n";
                finale = finale + commandReloadHelp + "\n";
                finale = finale + "\n";
                finale = finale + commandFooter;
                Main.messageGesturePaper.sendMessage(sender, finale);
                return;
            }
            switch (args[0]) {
                case "reload":
                    if (!sender.hasPermission("cagui.command.reload")) {
                        Main.messageGesturePaper.sendMessage(sender, errorInsufficientPermission);
                        return;
                    }
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
                        Main.messageGesturePaper.sendMessage(sender, "&6Reloading...");
                        ReloadGesture.reload(Main.instance.getName());
                        Main.messageGesturePaper.sendMessage(sender, "&aReloaded!");
                    });
                    break;
                default:
                    String finale = "&e&lCustomAnvilGUI &7● Version " + Main.instance.getDescription().getVersion()
                            + " created by eliotesta98 & xSavior_of_God" + "\n\n";
                    finale = finale + commandHelpHelp + "\n";
                    finale = finale + commandReloadHelp + "\n";
                    finale = finale + "\n";
                    finale = finale + commandFooter;
                    Main.messageGesturePaper.sendMessage(sender, finale);
                    break;
            }
        });
        return false;
    }
}

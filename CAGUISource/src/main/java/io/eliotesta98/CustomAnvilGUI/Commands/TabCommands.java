package io.eliotesta98.CustomAnvilGUI.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCommands implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commandList = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("customanvilgui")) {
            if (args.length == 1) {
                if(sender.hasPermission("cagui.command.reload")) {
                    commandList.add("reload");
                }
                commandList.add("help");
            }
        }
        return commandList;
    }
}

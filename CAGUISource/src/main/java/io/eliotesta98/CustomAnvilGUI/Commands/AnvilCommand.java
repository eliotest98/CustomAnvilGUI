package io.eliotesta98.CustomAnvilGUI.Commands;

import io.eliotesta98.CustomAnvilGUI.Core.Main;
import io.eliotesta98.CustomAnvilGUI.Interfaces.GuiEvent;
import io.eliotesta98.CustomAnvilGUI.Utils.OpenInventorySpigot;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

public class AnvilCommand implements Listener {

    private final String errorNoPlayer = Main.instance.getConfigGestion().getMessages().get("Errors.NoPlayer");
    private final String commandAnvil = Main.instance.getConfigGestion().getMessages().get("Commands.Anvil");
    private final String errorInsufficientPermission = Main.instance.getConfigGestion().getMessages().get("Errors.InsufficientPermission");

    private final List<String> anvilAliases = Main.instance.getConfigGestion().getAnvilAliases();

    private GuiEvent guiEvent;

    public AnvilCommand(GuiEvent guiEvent) {
        this.guiEvent = guiEvent;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerCommandPreprocessEvent e) {
        String[] args = e.getMessage().split(" ");
        onCommand(e.getPlayer(), args[0], Arrays.copyOfRange(args, 1, args.length), e);
    }

    public void onCommand(CommandSender sender, String command, String[] args, PlayerCommandPreprocessEvent e) {
        if (!anvilAliases.contains(command.replace("/", ""))) {
            return;
        }
        e.setCancelled(true);
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            if (!sender.hasPermission("cagui.command.anvil")) {
                Main.messageGesturePaper.sendMessage(sender, errorInsufficientPermission);
                return;
            }
            if (!(sender instanceof Player)) {
                Main.messageGesturePaper.sendMessage(sender, errorNoPlayer);
                return;
            }
            if (args.length > 1) {
                Main.messageGesturePaper.sendMessage(sender, commandAnvil);
                return;
            }
            guiEvent.addPlayerToCommand((Player) sender);
            if (args.length == 0) {
                Bukkit.getScheduler().runTask(Main.instance, () -> openAnvilSafely((Player) sender));
            } else {
                Bukkit.getScheduler().runTask(Main.instance, () -> {
                    guiEvent.addPlayerToWhitelist((Player) sender);
                    openAnvilSafely((Player) sender);
                });
            }
        });
    }

    private void openAnvilSafely(Player player) {
        try {
            OpenInventorySpigot.openInventory(player);
        } catch (Throwable ignore) {
        }
    }

    public void disableEvent() {
        PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
    }
}

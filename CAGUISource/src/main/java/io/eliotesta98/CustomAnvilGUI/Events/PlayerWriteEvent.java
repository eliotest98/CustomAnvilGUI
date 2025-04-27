package io.eliotesta98.CustomAnvilGUI.Events;

import io.eliotesta98.CustomAnvilGUI.Core.Main;
import io.eliotesta98.CustomAnvilGUI.Utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.HashMap;
import java.util.Map;

public class PlayerWriteEvent implements Listener {

    private final Map<String, ItemStack> renamingPlayers = new HashMap();
    private final String successfullyRename = Main.instance.getConfigGestion().getMessages().get("Success.Rename");

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerWrite(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String message = event.getMessage();
        if (renamingPlayers.containsKey(player.getName())) {
            if(renamingPlayers.get(player.getName()) == null) {
                renamingPlayers.replace(player.getName(), createItemStack(message));
                event.setCancelled(true);
                player.sendMessage(ColorUtils.applyColor(successfullyRename));
            }
        }
    }

    public boolean isPlayerRename(String playerName) {
        return renamingPlayers.containsKey(playerName);
    }

    public ItemStack getItemWithPlayerName(String playerName) {
        return renamingPlayers.get(playerName);
    }

    public void addPlayer(String playerName) {
        if (renamingPlayers.containsKey(playerName)) {
            renamingPlayers.replace(playerName, null);
        } else {
            renamingPlayers.put(playerName, null);
        }
    }

    public void removePlayer(String playerName) {
        renamingPlayers.remove(playerName);
    }

    public ItemStack createItemStack(String message) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = paper.getItemMeta();
        itemMeta.setDisplayName(message);
        paper.setItemMeta(itemMeta);
        return paper;
    }

    public void disableEvent() {
        if(renamingPlayers.isEmpty()) {
            AsyncPlayerChatEvent.getHandlerList().unregister(this);
        }
    }

}

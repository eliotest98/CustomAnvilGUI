package io.eliotesta98.CustomAnvilGUI.Events;

import com.HeroxWar.HeroxCore.MessageGesture;
import io.eliotesta98.CustomAnvilGUI.Core.Main;
import io.eliotesta98.CustomAnvilGUI.Interfaces.GuiEvent;
import io.eliotesta98.CustomAnvilGUI.Utils.ExpUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class PlayerWriteEvent implements Listener {

    private final Map<String, ItemStack> renamingPlayers = new HashMap<>();
    private final Map<String, ItemStack> itemInHands = new HashMap<>();
    private final String successfullyRename = Main.instance.getConfigGestion().getMessages().get("Success.Rename");
    private final String insufficientExp = Main.instance.getConfigGestion().getMessages().get("Errors.InsufficientExperience");
    private final boolean directRename = Main.instance.getConfigGestion().isDirectRename();
    private Location anvilLocation;
    private Inventory inv;

    public void setAnvilLocation(Location location) {
        anvilLocation = location;
    }

    public void setInventory(Inventory inv) {
        this.inv = inv;
    }

    public Location getAnvilLocation() {
        return anvilLocation;
    }

    public Inventory getInv() {
        return inv;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerWrite(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String message = event.getMessage();
        if (renamingPlayers.containsKey(player.getName())) {
            if (renamingPlayers.get(player.getName()) == null) {
                renamingPlayers.replace(player.getName(), createItemStack(message));
                if (directRename) {
                    int experienceRaw = ExpUtils.getExp(player);
                    double levels = ExpUtils.getLevelFromExp(experienceRaw);
                    if (levels >= 1) {
                        ItemStack itemInHand = itemInHands.get(player.getName());
                        player.getInventory().remove(itemInHand);
                        ItemMeta itemMeta = itemInHand.getItemMeta();
                        itemMeta.setDisplayName(message);
                        itemInHand.setItemMeta(itemMeta);
                        player.getInventory().addItem(itemInHand);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> GuiEvent.damageAnvil(player, anvilLocation, inv));
                        ExpUtils.changeExpLevels(player, -1);
                    } else {
                        MessageGesture.sendMessage(player, insufficientExp);
                    }
                    renamingPlayers.remove(player.getName());
                } else {
                    MessageGesture.sendMessage(player, successfullyRename);
                }
                event.setCancelled(true);
            }
        }
    }

    public boolean isPlayerRename(String playerName) {
        return renamingPlayers.containsKey(playerName);
    }

    public ItemStack getItemWithPlayerName(String playerName) {
        return renamingPlayers.get(playerName);
    }

    public ItemStack getItemInHand(String playerName) {
        return itemInHands.get(playerName);
    }

    public void addPlayer(String playerName, ItemStack itemInHand) {
        if (renamingPlayers.containsKey(playerName)) {
            renamingPlayers.replace(playerName, null);
            itemInHands.replace(playerName, itemInHand);
        } else {
            renamingPlayers.put(playerName, null);
            itemInHands.put(playerName, itemInHand);
        }
    }

    public void addPlayer(String playerName, ItemStack itemInHand, ItemStack itemStack) {
        if (renamingPlayers.containsKey(playerName)) {
            renamingPlayers.replace(playerName, itemStack);
            itemInHands.replace(playerName, itemInHand);
        } else {
            renamingPlayers.put(playerName, itemStack);
            itemInHands.put(playerName, itemInHand);
        }
    }

    public void removePlayer(String playerName) {
        renamingPlayers.remove(playerName);
        itemInHands.remove(playerName);
    }

    public ItemStack createItemStack(String message) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = paper.getItemMeta();
        itemMeta.setDisplayName(message);
        paper.setItemMeta(itemMeta);
        return paper;
    }

    public void disableEvent() {
        if (renamingPlayers.isEmpty()) {
            AsyncPlayerChatEvent.getHandlerList().unregister(this);
        }
    }

}

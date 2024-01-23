package io.eliotesta98.CustomGuiForAnvil.Events.CustomEvents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class CustomPrepareAnvilEvent extends Event implements Cancellable, Listener {

    private static final HandlerList handlerList = new HandlerList();
    private boolean isCancelled;
    private AnvilInventory anvilInventory;
    private ItemStack result;

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    // Spigot request
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    // Spigot request
    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public CustomPrepareAnvilEvent(AnvilInventory anvilInventory, ItemStack result) {
        this.isCancelled = false;
        this.anvilInventory = anvilInventory;
        this.result = result;
    }

    public AnvilInventory getAnvilInventory() {
        return anvilInventory;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }
}


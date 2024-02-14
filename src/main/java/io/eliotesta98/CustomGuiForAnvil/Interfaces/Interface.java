package io.eliotesta98.CustomGuiForAnvil.Interfaces;

import io.eliotesta98.CustomGuiForAnvil.Core.Main;
import io.eliotesta98.CustomGuiForAnvil.Utils.ColorUtils;
import io.eliotesta98.CustomGuiForAnvil.Utils.DebugUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Interface {

    private String title, soundOpen, nameInterface, nameInterfaceToOpen, nameInterfaceToReturn;
    private ArrayList<String> slots = new ArrayList<>();
    private HashMap<String, io.eliotesta98.CustomGuiForAnvil.Interfaces.ItemConfig> itemsConfig = new HashMap<>();
    private boolean debug;
    private int sizeModificableSlot;
    private final HashMap<String, Inventory> interfacesOpened = new HashMap<>();

    public Interface(String title, String soundOpen, ArrayList<String> slots, HashMap<String, io.eliotesta98.CustomGuiForAnvil.Interfaces.ItemConfig> itemsConfig, boolean debug, int sizeModificableSlot, String nameInterface, String nameInterfaceToOpen, String nameInterfaceToReturn) {
        this.title = title;
        this.soundOpen = soundOpen;
        this.itemsConfig.putAll(itemsConfig);
        this.debug = debug;
        this.sizeModificableSlot = sizeModificableSlot;
        this.slots.addAll(slots);
        this.nameInterface = nameInterface;
        this.nameInterfaceToOpen = nameInterfaceToOpen;
        this.nameInterfaceToReturn = nameInterfaceToReturn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSoundOpen() {
        return soundOpen;
    }

    public void setSoundOpen(String soundOpen) {
        this.soundOpen = soundOpen;
    }

    public ArrayList<String> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<String> slots) {
        this.slots = slots;
    }

    public HashMap<String, io.eliotesta98.CustomGuiForAnvil.Interfaces.ItemConfig> getItemsConfig() {
        return itemsConfig;
    }

    public void setItemsConfig(HashMap<String, io.eliotesta98.CustomGuiForAnvil.Interfaces.ItemConfig> itemsConfig) {
        this.itemsConfig = itemsConfig;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getSizeModificableSlot() {
        return sizeModificableSlot;
    }

    public void setSizeModificableSlot(int sizeModificableSlot) {
        this.sizeModificableSlot = sizeModificableSlot;
    }

    public String getNameInterface() {
        return nameInterface;
    }

    public void setNameInterface(String nameInterface) {
        this.nameInterface = nameInterface;
    }

    public String getNameInterfaceToOpen() {
        return nameInterfaceToOpen;
    }

    public void setNameInterfaceToOpen(String nameInterfaceToOpen) {
        this.nameInterfaceToOpen = nameInterfaceToOpen;
    }

    public String getNameInterfaceToReturn() {
        return nameInterfaceToReturn;
    }

    public void setNameInterfaceToReturn(String nameInterfaceToReturn) {
        this.nameInterfaceToReturn = nameInterfaceToReturn;
    }

    public void removeInventory(String playerName) {
        interfacesOpened.remove(playerName);
    }

    public void closeAllInventories() {
        for (Map.Entry<String, Inventory> inventory : interfacesOpened.entrySet()) {
            if (Bukkit.getPlayer(inventory.getKey()) == null) {
                continue;
            }
            Bukkit.getPlayer(inventory.getKey()).closeInventory();
        }
    }

    public void openInterface(Player p) {
        DebugUtils debug = new DebugUtils();
        long tempo = System.currentTimeMillis();
        CustomGuiForAnvil holder = new CustomGuiForAnvil(slots.size(), ColorUtils.applyColor(title));
        // prendo l'inventario
        final Inventory inventory = holder.getInventory();
        if (interfacesOpened.containsKey(p.getName())) {
            interfacesOpened.replace(p.getName(), inventory);
        } else {
            interfacesOpened.put(p.getName(), inventory);
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            for (int i = 0; i < slots.size(); i++) {// scorro gli slot
                String slot = slots.get(i);// prendo lo slot
                if (itemsConfig.get(slot) == null) {
                    continue;
                }
                inventory.setItem(i, itemsConfig.get(slot).createItemConfig(nameInterface, "", 0));
            }
            Main.instance.SoundManager.playSound(p, Sound.valueOf(soundOpen), 15.0f, 10.0f);
        });
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> p.openInventory(inventory));
        if (this.debug) {
            debug.addLine("ClickGui execution time= " + (System.currentTimeMillis() - tempo));
            debug.debug("ClickGui");
        }
    }

}

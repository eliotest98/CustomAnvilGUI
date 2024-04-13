package io.eliotesta98.AnvilPlus.Interfaces;

import io.eliotesta98.AnvilPlus.Core.Main;
import io.eliotesta98.AnvilPlus.Utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Interface {

    private String title, soundOpen, nameInterface, nameInterfaceToOpen, nameInterfaceToReturn;
    private ArrayList<String> slots = new ArrayList<>();
    private HashMap<String, io.eliotesta98.AnvilPlus.Interfaces.ItemConfig> itemsConfig = new HashMap<>();
    private boolean debug;
    private int sizeModificableSlot;
    private final Map<String, InventoryView> anvilInventories = new HashMap<>();
    private final Map<String, Integer> importantSlots = new HashMap<>();
    private final Map<String, String> importantSlotsLetter = new HashMap<>();

    public Interface(String title, String soundOpen, ArrayList<String> slots, HashMap<String, io.eliotesta98.AnvilPlus.Interfaces.ItemConfig> itemsConfig, boolean debug, int sizeModificableSlot, String nameInterface, String nameInterfaceToOpen, String nameInterfaceToReturn) {
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

    public HashMap<String, io.eliotesta98.AnvilPlus.Interfaces.ItemConfig> getItemsConfig() {
        return itemsConfig;
    }

    public void setItemsConfig(HashMap<String, io.eliotesta98.AnvilPlus.Interfaces.ItemConfig> itemsConfig) {
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

    public void removeInventory(String playerName, Inventory inventory, Location dropLocation, boolean removeOnly) {
        if (removeOnly) {
            anvilInventories.remove(playerName);
            return;
        }
        Interface customInterface = Main.instance.getConfigGestion().getInterfaces().get("Anvil");

        int itemSlot = customInterface.getImportantSlots().get("Item");
        ItemStack item = inventory.getItem(itemSlot);
        if (item != null) {
            dropLocation.getWorld().dropItem(dropLocation, item);
        }
        int enchantSlot = customInterface.getImportantSlots().get("Enchant");
        ItemStack enchantedBook = inventory.getItem(enchantSlot);
        if (enchantedBook != null) {
            dropLocation.getWorld().dropItem(dropLocation, enchantedBook);
        }
        anvilInventories.remove(playerName);
    }

    public InventoryView getInventoryFromName(String playerName) {
        return this.anvilInventories.get(playerName);
    }

    public Map<String, Integer> getImportantSlots() {
        return importantSlots;
    }

    public void closeAllInventories() {
        for (Map.Entry<String, InventoryView> inventory : anvilInventories.entrySet()) {
            Player player = Bukkit.getPlayer(inventory.getKey());
            if (player == null) {
                continue;
            }
            if (inventory.getValue().getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof CustomGuiForAnvil) {
                removeInventory(player.getName(), inventory.getValue().getPlayer().getOpenInventory().getTopInventory(), player.getLocation(), false);
            }
            player.closeInventory();
        }
    }

    public void openInterface(Player player, InventoryView anvil) {
        anvilInventories.put(player.getName(), anvil);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
            Inventory customAnvilInventory = getCustomAnvilInventory();
            player.openInventory(customAnvilInventory);
        });
    }

    public void setCostOfEnchant(Inventory inventory, int levels) {
        int slotToChange = importantSlots.get("Cost");
        inventory.setItem(slotToChange, this.getItemsConfig().get(importantSlotsLetter.get("Cost")).createItemConfig(this.getNameInterface(), "ap.experience:" + levels, slotToChange));
    }

    public void setBorder(Inventory inventory, int slot) {
        inventory.setItem(slot, this.itemsConfig.get(importantSlotsLetter.get("Border")).createItemConfig(this.getNameInterface(), "", slot));
    }

    public void deleteItemsWhenResult(Inventory inventory) {
        int slotToChange = importantSlots.get("Item");
        inventory.setItem(slotToChange, null);
        slotToChange = importantSlots.get("Enchant");
        inventory.setItem(slotToChange, null);
    }

    public void deleteResult(Inventory inventory) {
        int slotCost = importantSlots.get("Cost");
        setBorder(inventory, slotCost);
        int slotResult = importantSlots.get("Result");
        inventory.setItem(slotResult, null);
    }

    private Inventory getCustomAnvilInventory() {
        CustomGuiForAnvil holder = new CustomGuiForAnvil(this.getSlots().size(), ColorUtils.applyColor(this.getTitle()));
        // prendo l'inventario
        final Inventory inventory = holder.getInventory();

        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            for (int i = 0; i < this.getSlots().size(); i++) {// scorro gli slot
                String slot = this.getSlots().get(i);// prendo lo slot
                if (this.getItemsConfig().get(slot) == null) {
                    continue;
                }
                if (this.itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("Item")) {
                    importantSlots.putIfAbsent(this.itemsConfig.get(slot).getNameItemConfig(), i);
                    importantSlotsLetter.putIfAbsent(this.itemsConfig.get(slot).getNameItemConfig(), slot);
                } else if (this.itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("Enchant")) {
                    importantSlots.putIfAbsent(this.itemsConfig.get(slot).getNameItemConfig(), i);
                    importantSlotsLetter.putIfAbsent(this.itemsConfig.get(slot).getNameItemConfig(), slot);
                } else if (this.itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("Result")) {
                    importantSlots.putIfAbsent(this.itemsConfig.get(slot).getNameItemConfig(), i);
                    importantSlotsLetter.putIfAbsent(this.itemsConfig.get(slot).getNameItemConfig(), slot);
                } else if (this.itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("Cost")) {
                    importantSlots.putIfAbsent(this.itemsConfig.get(slot).getNameItemConfig(), i);
                    importantSlotsLetter.putIfAbsent(this.itemsConfig.get(slot).getNameItemConfig(), slot);
                    for (Map.Entry<String, ItemConfig> itemConfig : itemsConfig.entrySet()) {
                        if (itemConfig.getValue().getNameItemConfig().equalsIgnoreCase("Border")) {
                            inventory.setItem(i, itemConfig.getValue().createItemConfig(this.getNameInterface(), "", i));
                            break;
                        }
                    }
                } else {
                    importantSlotsLetter.putIfAbsent(this.itemsConfig.get(slot).getNameItemConfig(), slot);
                    inventory.setItem(i, this.getItemsConfig().get(slot).createItemConfig(this.getNameInterface(), "", i));
                }
            }
        });

        return inventory;
    }

}

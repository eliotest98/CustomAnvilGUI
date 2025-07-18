package io.eliotesta98.CustomAnvilGUI.Interfaces;

import com.HeroxWar.HeroxCore.MessageGesture;
import com.HeroxWar.HeroxCore.SoundGesture.SoundType;
import io.eliotesta98.CustomAnvilGUI.Core.Main;
import io.eliotesta98.CustomAnvilGUI.Events.PlayerWriteEvent;
import io.eliotesta98.CustomAnvilGUI.Utils.ExpUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.geysermc.cumulus.form.CustomForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interface {
    private String title, nameInterface, nameInterfaceToOpen, nameInterfaceToReturn;
    private SoundType soundOpen;
    private List<String> slots = new ArrayList<>();
    private Map<String, ItemConfig> itemsConfig = new HashMap<>();
    private boolean debug;
    private int sizeModifiableSlot;
    private final Map<String, InventoryView> anvilInventories = new HashMap<>();
    private final Map<String, Integer> importantSlots = new HashMap<>();
    private final Map<String, String> importantSlotsLetter = new HashMap<>();
    private final List<FloodgateInput> floodgateInputs;
    private String successRename;
    private String insufficientExp;
    private boolean directRename;

    public Interface(String title, SoundType soundOpen, ArrayList<String> slots, HashMap<String, ItemConfig> itemsConfig, List<FloodgateInput> floodgateInputs, boolean debug, int sizeModifiableSlot, String nameInterface, String nameInterfaceToOpen, String nameInterfaceToReturn) {
        this.title = MessageGesture.transformColor(title);
        this.soundOpen = soundOpen;
        this.itemsConfig.putAll(itemsConfig);
        this.debug = debug;
        this.sizeModifiableSlot = sizeModifiableSlot;
        this.slots.addAll(slots);
        this.nameInterface = nameInterface;
        this.nameInterfaceToOpen = nameInterfaceToOpen;
        this.nameInterfaceToReturn = nameInterfaceToReturn;
        this.floodgateInputs = floodgateInputs;
    }

    public void initialize(String successRename, boolean directRename, String insufficientExp) {
        this.successRename = successRename;
        this.directRename = directRename;
        this.insufficientExp = insufficientExp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SoundType getSoundOpen() {
        return soundOpen;
    }

    public void setSoundOpen(SoundType soundOpen) {
        this.soundOpen = soundOpen;
    }

    public List<String> getSlots() {
        return slots;
    }

    public void setSlots(List<String> slots) {
        this.slots = slots;
    }

    public Map<String, io.eliotesta98.CustomAnvilGUI.Interfaces.ItemConfig> getItemsConfig() {
        return itemsConfig;
    }

    public void setItemsConfig(Map<String, io.eliotesta98.CustomAnvilGUI.Interfaces.ItemConfig> itemsConfig) {
        this.itemsConfig = itemsConfig;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getSizeModifiableSlot() {
        return sizeModifiableSlot;
    }

    public void setSizeModifiableSlot(int sizeModifiableSlot) {
        this.sizeModifiableSlot = sizeModifiableSlot;
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
        Player player = Bukkit.getPlayer(playerName);
        int itemSlot = customInterface.getImportantSlots().get("FirstItem");
        ItemStack item = inventory.getItem(itemSlot);
        if (item != null) {
            if (player != null && player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            } else {
                dropLocation.getWorld().dropItem(dropLocation, item);
            }
        }
        int enchantSlot = customInterface.getImportantSlots().get("SecondItem");
        ItemStack enchantedBook = inventory.getItem(enchantSlot);
        if (enchantedBook != null && enchantedBook.getType() != Material.PAPER) {
            if (player != null && player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(enchantedBook);
            } else {
                dropLocation.getWorld().dropItem(dropLocation, enchantedBook);
            }
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
            Inventory topInventory = player.getOpenInventory().getTopInventory();
            if (topInventory.getHolder() instanceof CustomAnvilGUIHolder) {
                removeInventory(player.getName(), topInventory, player.getLocation(), false);
            }
            player.closeInventory();
        }
    }

    public void openInterface(Player player, InventoryView anvil) {
        anvilInventories.put(player.getName(), anvil);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
            Inventory customAnvilInventory = getCustomAnvilInventory(player);
            player.openInventory(customAnvilInventory);
            soundOpen.playSound(player);
        });
    }

    public void openInterface(Player player, PlayerWriteEvent event) {
        if (!Main.floodgateUtils.isBedrockPlayer(player.getUniqueId())) {
            return;
        }
        soundOpen.playSound(player);
        Main.floodgateUtils.getBedrockPlayer(player.getUniqueId()).sendForm(createCustomForm(player, event).build());
    }

    private CustomForm.Builder createCustomForm(Player player, PlayerWriteEvent event) {
        CustomForm.Builder customForm = CustomForm.builder().title(title);
        for (FloodgateInput floodgateInput : floodgateInputs) {
            switch (floodgateInput.getType()) {
                case "Input":
                    customForm.input(floodgateInput.getLabel(), floodgateInput.getPlaceholder());
                    break;
                case "Button":
                    break;
                default:
                    break;
            }
        }
        customForm.validResultHandler(response -> {
                    if (directRename) {
                        int experienceRaw = ExpUtils.getExp(player);
                        double levels = ExpUtils.getLevelFromExp(experienceRaw);
                        if (levels >= 1) {
                            ItemStack itemInHand = event.getItemInHand(player.getName());
                            player.getInventory().remove(itemInHand);
                            ItemMeta itemMeta = itemInHand.getItemMeta();
                            itemMeta.setItemName(response.asInput(0));
                            itemInHand.setItemMeta(itemMeta);
                            player.getInventory().addItem(itemInHand);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> GuiEvent.damageAnvil(player, event.getAnvilLocation(), event.getInv()));
                            ExpUtils.changeExpLevels(player, -1);
                        } else {
                            MessageGesture.sendMessage(player, insufficientExp);
                        }
                        event.removePlayer(player.getName());
                    } else {
                        event.addPlayer(player.getName(), event.getItemInHand(player.getName()), event.createItemStack(response.asInput(0)));
                        MessageGesture.sendMessage(player, successRename);
                    }
                })
                .closedResultHandler(() -> event.removePlayer(player.getName()))
                .invalidResultHandler(() -> event.removePlayer(player.getName()));
        return customForm;
    }

    public void setCostOfEnchant(Inventory inventory, int levels) {
        int slotToChange = importantSlots.get("Cost");
        inventory.setItem(slotToChange, this.getItemsConfig().get(importantSlotsLetter.get("Cost")).createItemConfig(this.getNameInterface(), "ap.experience:" + levels, slotToChange));
    }

    public void setBorder(Inventory inventory, int slot) {
        inventory.setItem(slot, this.itemsConfig.get(importantSlotsLetter.get("Border")).createItemConfig(this.getNameInterface(), "", slot));
    }

    public void deleteItemsWhenResult(Inventory inventory) {
        int slotToChange = importantSlots.get("FirstItem");
        inventory.setItem(slotToChange, null);
        slotToChange = importantSlots.get("SecondItem");
        inventory.setItem(slotToChange, null);
    }

    public void deleteResult(Inventory inventory) {
        int slotCost = importantSlots.get("Cost");
        setBorder(inventory, slotCost);
        setBarrier(inventory, " ");
    }

    public void setBarrier(Inventory inventory, String reason) {
        int slotResult = importantSlots.get("NoResult");
        inventory.setItem(slotResult, getItemsConfig().get(importantSlotsLetter.get("NoResult")).createItemConfig(getNameInterface(), "ap.message:" + reason, slotResult));
    }

    public void setRename(Inventory inventory, Player player, PlayerWriteEvent event, ItemStack itemToRename) {
        int slotToChange = importantSlots.get("FirstItem");
        inventory.setItem(slotToChange, itemToRename);
        slotToChange = importantSlots.get("SecondItem");
        inventory.setItem(slotToChange, event.getItemWithPlayerName(player.getName()));
    }

    private Inventory getCustomAnvilInventory(Player player) {
        CustomAnvilGUIHolder holder = new CustomAnvilGUIHolder(this.getSlots().size(), getTitle());
        // prendo l'inventario
        final Inventory inventory = holder.getInventory();

        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            for (int i = 0; i < getSlots().size(); i++) {// scorro gli slot
                String slot = getSlots().get(i);// prendo lo slot
                if (getItemsConfig().get(slot) == null) {
                    continue;
                }
                if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("FirstItem")) {
                    importantSlots.putIfAbsent(itemsConfig.get(slot).getNameItemConfig(), i);
                    importantSlotsLetter.putIfAbsent(itemsConfig.get(slot).getNameItemConfig(), slot);
                } else if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("SecondItem")) {
                    importantSlots.putIfAbsent(itemsConfig.get(slot).getNameItemConfig(), i);
                    importantSlotsLetter.putIfAbsent(itemsConfig.get(slot).getNameItemConfig(), slot);
                } else if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("NoResult")) {
                    importantSlots.putIfAbsent(itemsConfig.get(slot).getNameItemConfig(), i);
                    importantSlotsLetter.putIfAbsent(itemsConfig.get(slot).getNameItemConfig(), slot);
                    inventory.setItem(i, getItemsConfig().get(slot).createItemConfig(getNameInterface(), "ap.message: ", i));
                } else if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("Cost")) {
                    importantSlots.putIfAbsent(itemsConfig.get(slot).getNameItemConfig(), i);
                    importantSlotsLetter.putIfAbsent(itemsConfig.get(slot).getNameItemConfig(), slot);
                    for (Map.Entry<String, ItemConfig> itemConfig : itemsConfig.entrySet()) {
                        if (itemConfig.getValue().getNameItemConfig().equalsIgnoreCase("Border")) {
                            inventory.setItem(i, itemConfig.getValue().createItemConfig(getNameInterface(), "", i));
                            break;
                        }
                    }
                } else if (itemsConfig.get(slot).getNameItemConfig().equalsIgnoreCase("Back")) {
                    if (!Main.floodgateUtils.isBedrockPlayer(player.getUniqueId())) {
                        importantSlotsLetter.putIfAbsent(itemsConfig.get(slot).getNameItemConfig(), slot);
                        inventory.setItem(i, getItemsConfig().get(slot).createItemConfig(getNameInterface(), "", i));
                    } else {
                        for (Map.Entry<String, ItemConfig> itemConfig : itemsConfig.entrySet()) {
                            if (itemConfig.getValue().getNameItemConfig().equalsIgnoreCase("Border")) {
                                inventory.setItem(i, itemConfig.getValue().createItemConfig(getNameInterface(), "", i));
                                break;
                            }
                        }
                    }
                } else {
                    importantSlotsLetter.putIfAbsent(itemsConfig.get(slot).getNameItemConfig(), slot);
                    inventory.setItem(i, getItemsConfig().get(slot).createItemConfig(getNameInterface(), "", i));
                }
            }
        });

        return inventory;
    }

}

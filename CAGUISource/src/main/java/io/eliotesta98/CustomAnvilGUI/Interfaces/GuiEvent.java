package io.eliotesta98.CustomAnvilGUI.Interfaces;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.eliotesta98.CustomAnvilGUI.Core.Main;
import io.eliotesta98.CustomAnvilGUI.Event.PlayerWriteEvent;
import io.eliotesta98.CustomAnvilGUI.Utils.ColorUtils;
import io.eliotesta98.CustomAnvilGUI.Utils.DebugUtils;
import io.eliotesta98.CustomAnvilGUI.Utils.ExpUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiEvent implements Listener {

    private final PlayerWriteEvent playerWriteEvent = new PlayerWriteEvent();
    private final boolean debugGui = Main.instance.getConfigGestion().getDebug().get("ClickGui");
    private final String insufficientExp = Main.instance.getConfigGestion().getMessages().get("Errors.InsufficientExperience");
    private final String renameInfo = Main.instance.getConfigGestion().getMessages().get("Info.Rename");
    private final List<String> whitelistedPlayers = new ArrayList<>();
    private final DebugUtils debugUtils = new DebugUtils();

    @EventHandler
    public void onAnvilInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType().toString().equalsIgnoreCase("ANVIL")
                && !whitelistedPlayers.contains(event.getPlayer().getName())) {
            Main.instance.getConfigGestion().getInterfaces().get("Anvil").openInterface((Player) event.getPlayer(), event.getView());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();
        if (event.getInventory().getHolder() instanceof CustomAnvilGUIHolder) {
            Main.instance.getConfigGestion().getInterfaces().get("Anvil")
                    .removeInventory(event.getPlayer().getName(), event.getInventory(), event.getPlayer().getLocation(), false);
        }
        if (event.getInventory().getType().toString().equalsIgnoreCase("ANVIL")) {
            whitelistedPlayers.remove(event.getPlayer().getName());
        }
        if (debugGui) {
            debug.addLine("Gui execution time= " + (System.currentTimeMillis() - time));
            debug.debug("InventoryCloseEvent");
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();

        if (event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof CustomAnvilGUIHolder) {
            Main.instance.getConfigGestion().getInterfaces().get("Anvil").removeInventory(event.getPlayer().getName(), event.getPlayer().getOpenInventory().getTopInventory(), event.getPlayer().getLocation(), true);
        }
        if (debugGui) {
            debug.addLine("Gui execution time= " + (System.currentTimeMillis() - time));
            debug.debug("PlayerQuitEvent");
        }
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent event) {
        DebugUtils debug = new DebugUtils();
        long time = System.currentTimeMillis();

        if (event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof CustomAnvilGUIHolder) {
            Main.instance.getConfigGestion().getInterfaces().get("Anvil").removeInventory(event.getPlayer().getName(), event.getPlayer().getOpenInventory().getTopInventory(), event.getPlayer().getLocation(), true);
        }
        if (debugGui) {
            debug.addLine("Gui execution time= " + (System.currentTimeMillis() - time));
            debug.debug("PlayerKickEvent");
        }
    }

    /*
     * In API versions 1.20.6 and earlier, InventoryView is a class.
     * In versions 1.21 and later, it is an interface.
     * This method uses reflection to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.
     *
     * @param view The generic InventoryView.
     * @return The top Inventory object from the event's InventoryView.
     */
    public static Inventory getTopInventory(Object view) {
        try {
            Method getTopInventory = view.getClass().getMethod("getTopInventory");
            getTopInventory.setAccessible(true);
            return (Inventory) getTopInventory.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();

        if (inv == null) {
            if (debugGui) {
                debugUtils.addLine("Inventory is null");
                debugUtils.debug("Click Gui");
            }
            return;
        }

        if (inv.getHolder() instanceof CustomAnvilGUIHolder) {

            Player p = (Player) event.getWhoClicked();

            InventoryView inventoryView = Main.instance.getConfigGestion().getInterfaces().get("Anvil").getInventoryFromName(p.getName());
            if (inventoryView == null) {
                event.setCancelled(true);
                if (debugGui) {
                    debugUtils.addLine("Inventory View is null");
                    debugUtils.debug("Click Gui");
                }
                return;
            }
            Inventory topInventory = getTopInventory(inventoryView);
            Location anvilLocation = topInventory.getLocation();
            Material anvilType = anvilLocation.getBlock().getType();
            if (anvilType != Material.ANVIL && anvilType != Material.DAMAGED_ANVIL && anvilType != Material.CHIPPED_ANVIL) {
                p.closeInventory();
                if (debugGui) {
                    debugUtils.addLine("Anvil block not exist");
                    debugUtils.debug("Click Gui");
                }
                return;
            }

            String typeInterface = "Anvil";
            Interface customInterface = Main.instance.getConfigGestion().getInterfaces().get(typeInterface);
            ArrayList<String> slots = customInterface.getSlots();
            String nameItemConfig = customInterface.getItemsConfig().get(slots.get(event.getSlot())).getNameItemConfig();
            if (event.getCurrentItem() != null && customInterface.getItemsConfig().get(slots.get(event.getSlot())).getType().equalsIgnoreCase(event.getCurrentItem().getType().toString())) {
                event.setCancelled(true);
                // Back
                if (nameItemConfig.equalsIgnoreCase("Back")) {
                    p.closeInventory();
                    topInventory.setItem(0, null);
                    topInventory.setItem(1, null);
                    whitelistedPlayers.add(p.getName());
                    Main.instance.getConfigGestion().getInterfaces().get("Anvil").removeInventory(p.getName(), event.getClickedInventory(), p.getLocation(), true);
                    try {
                        p.openInventory(inventoryView);
                    } catch (IllegalArgumentException ignore) {

                    }
                }
                // Submit
                else if (nameItemConfig.equalsIgnoreCase("Submit")) {
                    topInventory.clear();
                    ItemStack firstItem = inv.getItem(Main.instance.getConfigGestion().getInterfaces().get("Anvil")
                            .getImportantSlots().get("FirstItem"));
                    if (firstItem == null) {
                        firstItem = new ItemStack(Material.AIR);
                    }
                    ItemStack secondItem = inv.getItem(Main.instance.getConfigGestion().getInterfaces().get("Anvil")
                            .getImportantSlots().get("SecondItem"));
                    if (secondItem == null) {
                        secondItem = new ItemStack(Material.AIR);
                    } else if (secondItem.getType() == Material.PAPER) {
                        ItemStack renamedItem = new ItemStack(firstItem);
                        ItemMeta meta = renamedItem.getItemMeta();
                        meta.setDisplayName(secondItem.getItemMeta().getDisplayName());
                        renamedItem.setItemMeta(meta);
                        NBTItem nbtItem = new NBTItem(renamedItem);
                        nbtItem.setInteger("ap.repairCost", 1);
                        customInterface.setCostOfEnchant(inv, 1);
                        inv.setItem(Main.instance.getConfigGestion().getInterfaces().get("Anvil").getImportantSlots().get("NoResult"), nbtItem.getItem());
                        return;
                    }
                    topInventory.setItem(0, firstItem);
                    topInventory.setItem(1, secondItem);
                }
                // Rename
                else if (nameItemConfig.equalsIgnoreCase("Rename")) {
                    // se il primo slot contiene l'item e il secondo slot è vuoto
                    if (inv.getItem(Main.instance.getConfigGestion().getInterfaces().get("Anvil").getImportantSlots()
                            .get("FirstItem")) != null && inv.getItem(Main.instance.getConfigGestion().getInterfaces()
                            .get("Anvil").getImportantSlots().get("SecondItem")) == null) {
                        Bukkit.getServer().getPluginManager().registerEvents(playerWriteEvent, Main.instance);
                        if (playerWriteEvent.isPlayerRename(p.getName())) {
                            inv.setItem(Main.instance.getConfigGestion().getInterfaces().get("Anvil").getImportantSlots()
                                    .get("SecondItem"), playerWriteEvent.getItemWithPlayerName(p.getName()));
                            playerWriteEvent.removePlayer(p.getName());
                            playerWriteEvent.disableEvent();
                        } else {
                            playerWriteEvent.addPlayer(p.getName());
                            p.closeInventory();
                            p.sendMessage(ColorUtils.applyColor(renameInfo));
                        }
                    }
                }
                if (debugGui) {
                    debugUtils.addLine("The item is not null");
                    debugUtils.addLine("The item is equals configuration in config.yml");
                    debugUtils.debug("Click Gui");
                }
            } else if (event.getCurrentItem() != null) {
                if (debugGui) {
                    debugUtils.addLine("The item is not null");
                    debugUtils.addLine("Activate Section:" + nameItemConfig);
                    debugUtils.debug("Click Gui");
                }

                ItemStack firstItem = inv.getItem(Main.instance.getConfigGestion().getInterfaces().get("Anvil").getImportantSlots().get("SecondItem"));

                // Cost
                if (nameItemConfig.equalsIgnoreCase("Cost")) {
                    event.setCancelled(true);
                }
                // First Item (Rename)
                else if (nameItemConfig.equalsIgnoreCase("FirstItem") && firstItem != null && firstItem.getType() == Material.PAPER) {
                    customInterface.setBarrier(inv);
                    customInterface.setBorder(inv, customInterface.getImportantSlots().get("Cost"));
                }
                // Item
                else if (nameItemConfig.equalsIgnoreCase("FirstItem")) {
                    customInterface.deleteResult(inv);
                    topInventory.setItem(0, null);
                }
                // Second Item (Rename)
                else if (nameItemConfig.equalsIgnoreCase("SecondItem") && firstItem != null && firstItem.getType() == Material.PAPER) {
                    event.setCancelled(true);
                    inv.setItem(event.getSlot(), null);
                }
                // Enchant
                else if (nameItemConfig.equalsIgnoreCase("SecondItem")) {
                    customInterface.deleteResult(inv);
                    topInventory.setItem(1, null);
                }
                // Result
                else if (nameItemConfig.equalsIgnoreCase("NoResult")) {
                    event.setCancelled(true);
                    int repairCost = 0;
                    if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                        NBTItem nbtItem = new NBTItem(event.getCurrentItem());
                        repairCost = nbtItem.getInteger("ap.repairCost");
                    }
                    int slotResult = customInterface.getImportantSlots().get("NoResult");
                    ItemStack result = inv.getItem(slotResult);
                    int experienceRaw = ExpUtils.getExp(p);
                    double levels = ExpUtils.getLevelFromExp(experienceRaw);
                    if (levels >= repairCost) {
                        if (result != null) {
                            ExpUtils.changeExpLevels(p, -repairCost);
                            if (p.getInventory().firstEmpty() != -1) {
                                p.getInventory().addItem(result);
                            } else {
                                p.getWorld().dropItem(p.getLocation(), result);
                            }
                            customInterface.setBarrier(inv);
                            customInterface.setBorder(inv, customInterface.getImportantSlots().get("Cost"));
                            customInterface.deleteItemsWhenResult(inv);
                            damageAnvil(anvilLocation);
                        }
                    } else {
                        p.sendMessage(ColorUtils.applyColor(insufficientExp));
                    }
                }
            }
        }
    }

    /*
     * In API versions 1.20.6 and earlier, InventoryView is a class.
     * In versions 1.21 and later, it is an interface.
     * This method uses reflection to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.
     *
     * @param view The generic InventoryView.
     * @return The top Inventory object from the event's InventoryView.
     */
    public static Object getInventoryInfo(InventoryEvent event, String method) {
        try {
            Object view = event.getView();
            Method getPlayer = view.getClass().getMethod(method);
            getPlayer.setAccessible(true);
            return getPlayer.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onInventoryPrepareAnvilEvent(PrepareAnvilEvent event) {
        Player player = (Player) getInventoryInfo(event, "getPlayer");
        Inventory topInventory = getTopInventory(player.getOpenInventory());
        if (topInventory.getHolder() instanceof CustomAnvilGUIHolder) {
            Interface interface_ = Main.instance.getConfigGestion().getInterfaces().get("Anvil");
            int repairCost = -1;
            try {
                repairCost = (int) getInventoryInfo(event, "getRepairCost");
            } catch (RuntimeException exception) {
                repairCost = event.getInventory().getRepairCost();
            }
            String renameText = "";
            try {
                renameText = (String) getInventoryInfo(event, "getRenameText");
            } catch (RuntimeException exception) {
                renameText = event.getInventory().getRenameText();
            }

            if (debugGui) {
                debugUtils.addLine("Custom Anvil Gui");
                debugUtils.addLine("Repair Cost: " + repairCost);
                debugUtils.addLine("Item Result: " + event.getResult());
                debugUtils.addLine("Rename: " + renameText);
                debugUtils.debug("PrepareAnvilEvent");
            }

            if (repairCost > 0) {
                if (event.getResult() != null && event.getResult().getType() != Material.AIR) {
                    interface_.setCostOfEnchant(topInventory, repairCost);

                    NBTItem nbtItem = new NBTItem(event.getResult());
                    nbtItem.setInteger("ap.repairCost", repairCost);
                    topInventory.setItem(interface_.getImportantSlots().get("NoResult"), nbtItem.getItem());
                }
            } else {
                interface_.deleteResult(topInventory);
            }
        }
    }

    private void damageAnvil(Location anvilLocation) {
        Random random = new Random();
        int extractedNumber = random.nextInt(100);
        if (extractedNumber <= 12) {
            Material anvilType = anvilLocation.getBlock().getType();
            if (anvilType == Material.ANVIL) {
                Directional blockData = (Directional) anvilLocation.getBlock().getBlockData();
                BlockFace blockFace = blockData.getFacing();
                anvilLocation.getBlock().setType(Material.CHIPPED_ANVIL);
                blockData = (Directional) anvilLocation.getBlock().getBlockData();
                blockData.setFacing(blockFace);
                anvilLocation.getBlock().setBlockData(blockData);
//                try {
//                    org.bukkit.block.data.Directional blockData = (org.bukkit.block.data.Directional) anvilLocation.getBlock().getBlockData();
//                    BlockFace blockFace = blockData.getFacing();
//                    anvilLocation.getBlock().setType(Material.CHIPPED_ANVIL);
//                    blockData = (org.bukkit.block.data.Directional) anvilLocation.getBlock().getBlockData();
//                    blockData.setFacing(blockFace);
//                    anvilLocation.getBlock().setBlockData(blockData);
//                } catch (NoClassDefFoundError error) {
//                    org.bukkit.material.Directional blockData = (org.bukkit.material.Directional) anvilLocation.getBlock().getState().getData();
//                    BlockFace blockFace = blockData.getFacing();
//                    anvilLocation.getBlock().setType(Material.ANVIL);
//                    blockData = (org.bukkit.material.Directional) anvilLocation.getBlock().getState().getData();
//                    blockData.setFacingDirection(blockFace);
//                    ((org.bukkit.material.Directional) anvilLocation.getBlock().getState().getData()).setFacingDirection(blockFace);
//                }
                Main.instance.soundManager.playSound(anvilLocation, "BLOCK_ANVIL_USE", 100f, 100f);
            } else if (anvilType == Material.CHIPPED_ANVIL) {
                Directional blockData = (Directional) anvilLocation.getBlock().getBlockData();
                BlockFace blockFace = blockData.getFacing();
                anvilLocation.getBlock().setType(Material.DAMAGED_ANVIL);
                blockData = (Directional) anvilLocation.getBlock().getBlockData();
                blockData.setFacing(blockFace);
                anvilLocation.getBlock().setBlockData(blockData);
//                try {
//                    org.bukkit.block.data.Directional blockData = (org.bukkit.block.data.Directional) anvilLocation.getBlock().getBlockData();
//                    BlockFace blockFace = blockData.getFacing();
//                    anvilLocation.getBlock().setType(Material.DAMAGED_ANVIL);
//                    blockData = (org.bukkit.block.data.Directional) anvilLocation.getBlock().getBlockData();
//                    blockData.setFacing(blockFace);
//                    anvilLocation.getBlock().setBlockData(blockData);
//                } catch (NoClassDefFoundError error) {
//                    org.bukkit.material.Directional blockData = (org.bukkit.material.Directional) anvilLocation.getBlock().getState().getData();
//                    BlockFace blockFace = blockData.getFacing();
//                    anvilLocation.getBlock().setType(Material.DAMAGED_ANVIL);
//                    blockData = (org.bukkit.material.Directional) anvilLocation.getBlock().getState().getData();
//                    blockData.setFacingDirection(blockFace);
//                    ((org.bukkit.material.Directional) anvilLocation.getBlock().getState().getData()).setFacingDirection(blockFace);
//                }
                Main.instance.soundManager.playSound(anvilLocation, "BLOCK_ANVIL_USE", 100f, 100f);
            } else if (anvilType == Material.DAMAGED_ANVIL) {
                anvilLocation.getBlock().setType(Material.AIR);
                Main.instance.soundManager.playSound(anvilLocation, "BLOCK_ANVIL_USE", 100f, 100f);
            }
        } else {
            Main.instance.soundManager.playSound(anvilLocation, "BLOCK_ANVIL_USE", 100f, 100f);
        }
    }
}
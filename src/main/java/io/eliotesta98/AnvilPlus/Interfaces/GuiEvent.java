package io.eliotesta98.AnvilPlus.Interfaces;

import de.tr7zw.nbtapi.NBTItem;
import io.eliotesta98.AnvilPlus.Core.Main;
import io.eliotesta98.AnvilPlus.Utils.ColorUtils;
import io.eliotesta98.AnvilPlus.Utils.DebugUtils;
import io.eliotesta98.AnvilPlus.Utils.ExpUtils;
import io.eliotesta98.AnvilPlus.Utils.SoundManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Structure;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GuiEvent implements Listener {

    private final boolean debugGui = Main.instance.getConfigGestion().getDebug().get("ClickGui");
    private final String insufficientExp = Main.instance.getConfigGestion().getMessages().get("Errors.InsufficientExperience");
    private final Map<Location, ArrayList<String>> anvilsLocations = new HashMap<>();
    private final Map<Location, Integer> anvilsDamage = new HashMap<>();
    private final List<String> whitelistedPlayers = new ArrayList<>();

    @EventHandler
    public void onAnvilPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType().toString().contains("ANVIL")) {
            NBTItem nbtItem = new NBTItem(event.getItemInHand());
            if (nbtItem.hasTag("ap.probabilityToDamage")) {
                anvilsDamage.put(event.getBlockPlaced().getLocation(), nbtItem.getInteger("ap.probabilityToDamage"));
            } else {
                anvilsDamage.put(event.getBlockPlaced().getLocation(), 12);
            }
        }
    }

    @EventHandler
    public void onAnvilBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().toString().contains("ANVIL")) {
            if (anvilsDamage.containsKey(event.getBlock().getLocation())) {
                int probabilityToDamage = anvilsDamage.get(event.getBlock().getLocation());
                for (ItemStack itemStack : event.getBlock().getDrops(event.getPlayer().getItemInHand())) {
                    if (itemStack.getType().toString().contains("ANVIL")) {
                        event.setDropItems(false);
                        NBTItem nbtItem = new NBTItem(itemStack);
                        nbtItem.setInteger("ap.probabilityToDamage", probabilityToDamage);
                        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), nbtItem.getItem());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onAnvilClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType().toString().contains("ANVIL")) {
            if (!anvilsLocations.containsKey(event.getClickedBlock().getLocation())) {
                anvilsLocations.put(event.getClickedBlock().getLocation(), new ArrayList<>());
            }
            anvilsLocations.get(event.getClickedBlock().getLocation()).add(event.getPlayer().getName());
        }
    }

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
        if (event.getInventory().getHolder() instanceof CustomGuiForAnvil) {
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

        if (event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof CustomGuiForAnvil) {
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

        if (event.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof CustomGuiForAnvil) {
            Main.instance.getConfigGestion().getInterfaces().get("Anvil").removeInventory(event.getPlayer().getName(), event.getPlayer().getOpenInventory().getTopInventory(), event.getPlayer().getLocation(), true);
        }
        if (debugGui) {
            debug.addLine("Gui execution time= " + (System.currentTimeMillis() - time));
            debug.debug("PlayerKickEvent");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        DebugUtils debugUtils = new DebugUtils();

        Inventory inv = event.getClickedInventory();

        if (inv == null) {
            if (debugGui) {
                debugUtils.addLine("Inventory is null");
                debugUtils.debug("Click Gui");
            }
            return;
        }

        if (inv.getHolder() instanceof CustomGuiForAnvil) {

            Player p = (Player) event.getWhoClicked();

            for (Map.Entry<Location, ArrayList<String>> anvilLocation : anvilsLocations.entrySet()) {
                for (String playerName : anvilLocation.getValue()) {
                    if (playerName.equalsIgnoreCase(p.getName())) {
                        if (anvilLocation.getKey().getBlock().getType() == Material.AIR) {
                            if (p.isOnline()) {
                                if (p.getOpenInventory().getTopInventory().getHolder() instanceof CustomGuiForAnvil) {
                                    p.closeInventory();
                                }
                            }
                        }
                        break;
                    }
                }
            }

            InventoryView inventoryView = Main.instance.getConfigGestion().getInterfaces().get("Anvil").getInventoryFromName(p.getName());
            if (inventoryView == null) {
                event.setCancelled(true);
                if (debugGui) {
                    debugUtils.addLine("Inventory View is null");
                    debugUtils.debug("Click Gui");
                }
                return;
            }

            String typeInterface = "Anvil";
            Interface customInterface = Main.instance.getConfigGestion().getInterfaces().get(typeInterface);
            ArrayList<String> slots = customInterface.getSlots();
            String nameItemConfig = customInterface.getItemsConfig().get(slots.get(event.getSlot())).getNameItemConfig();
            if (event.getCurrentItem() != null && customInterface.getItemsConfig().get(slots.get(event.getSlot())).getType().equalsIgnoreCase(event.getCurrentItem().getType().toString())) {
                if (nameItemConfig.equalsIgnoreCase("Back")) {
                    p.closeInventory();
                    whitelistedPlayers.add(p.getName());
                    Main.instance.getConfigGestion().getInterfaces().get("Anvil").removeInventory(p.getName(), event.getClickedInventory(), p.getLocation(), true);
                    p.openInventory(inventoryView);
                } else if (nameItemConfig.equalsIgnoreCase("Confirm")) {
                    inventoryView.getTopInventory().setItem(0,
                            inv.getItem(Main.instance.getConfigGestion().getInterfaces().get("Anvil")
                                    .getImportantSlots().get("Item")));
                    inventoryView.getTopInventory().setItem(1,
                            inv.getItem(Main.instance.getConfigGestion().getInterfaces().get("Anvil")
                                    .getImportantSlots().get("Enchant")));
                }
                event.setCancelled(true);
                if (debugGui) {
                    debugUtils.addLine("The item is not null");
                    debugUtils.addLine("The item is equals configurated in config.yml");
                    debugUtils.debug("Click Gui");
                }
            } else if (event.getCurrentItem() != null) {
                if (debugGui) {
                    debugUtils.addLine("The item is not null");
                    debugUtils.addLine("Activate Section:" + nameItemConfig);
                    debugUtils.debug("Click Gui");
                }
                // Cost
                if (nameItemConfig.equalsIgnoreCase("Cost")) {
                    event.setCancelled(true);
                }
                // Item
                else if (nameItemConfig.equalsIgnoreCase("Item")) {
                    customInterface.deleteResult(inv);
                }
                // Enchant
                else if (nameItemConfig.equalsIgnoreCase("Enchant")) {
                    customInterface.deleteResult(inv);
                }
                // Result
                else if (nameItemConfig.equalsIgnoreCase("Result")) {
                    int repairCost = 0;
                    if (event.getCurrentItem() != null) {
                        NBTItem nbtItem = new NBTItem(event.getCurrentItem());
                        repairCost = nbtItem.getInteger("ap.repairCost");
                    }
                    int slotResult = customInterface.getImportantSlots().get("Result");
                    ItemStack result = inv.getItem(slotResult);
                    int experienceRaw = ExpUtils.getExp(p);
                    double levels = ExpUtils.getLevelFromExp(experienceRaw);
                    if (levels >= repairCost) {
                        if (result != null) {
                            ExpUtils.changeExp(p, -ExpUtils.getExpFromLevel(repairCost));
                            if (p.getInventory().firstEmpty() != -1) {
                                p.getInventory().addItem(result);
                            } else {
                                p.getWorld().dropItem(p.getLocation(), result);
                            }
                            customInterface.setBorder(inv, event.getSlot());
                            customInterface.setBorder(inv, customInterface.getImportantSlots().get("Cost"));
                            customInterface.deleteItemsWhenResult(inv);

                            damageAnvil(p.getName());

                            event.setCancelled(true);
                        }
                    } else {
                        p.sendMessage(ColorUtils.applyColor(insufficientExp));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryPrepareAnvilEvent(PrepareAnvilEvent event) {
        if (event.getView().getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof CustomGuiForAnvil) {
            Interface interface_ = Main.instance.getConfigGestion().getInterfaces().get("Anvil");
            AnvilInventory anvil = event.getInventory();

            if (anvil.getRepairCost() > 0) {
                if (event.getResult() != null) {
                    interface_.setCostOfEnchant(
                            event.getView().getPlayer().getOpenInventory().getTopInventory(), anvil.getRepairCost());

                    NBTItem nbtItem = new NBTItem(event.getResult());
                    nbtItem.setInteger("ap.repairCost", anvil.getRepairCost());
                    event.getView().getPlayer().getOpenInventory().getTopInventory().setItem(
                            interface_.getImportantSlots().get("Result"), nbtItem.getItem());
                }
            } else {
                interface_.deleteResult(event.getView().getPlayer().getOpenInventory().getTopInventory());
            }
        }
    }

    private void damageAnvil(String anvilOpenerName) {
        for (Map.Entry<Location, ArrayList<String>> anvilLocation : anvilsLocations.entrySet()) {
            for (String playerName : anvilLocation.getValue()) {
                if (playerName.equalsIgnoreCase(anvilOpenerName)) {
                    int anvilDamage = anvilsDamage.get(anvilLocation.getKey());
                    Random random = new Random();
                    int extractedNumber = random.nextInt(100);
                    if (extractedNumber < anvilDamage) {
                        Material anvilType = anvilLocation.getKey().getBlock().getType();
                        if (anvilType == Material.ANVIL) {
                            anvilLocation.getKey().getBlock().setType(Material.CHIPPED_ANVIL);
                            SoundManager.playSound(anvilLocation.getKey(), Sound.BLOCK_ANVIL_USE, 100f, 100f);
                            anvilsDamage.replace(anvilLocation.getKey(), 12);
                        } else if (anvilType == Material.CHIPPED_ANVIL) {
                            anvilLocation.getKey().getBlock().setType(Material.DAMAGED_ANVIL);
                            SoundManager.playSound(anvilLocation.getKey(), Sound.BLOCK_ANVIL_USE, 100f, 100f);
                            anvilsDamage.replace(anvilLocation.getKey(), 12);
                        } else if (anvilType == Material.DAMAGED_ANVIL) {
                            anvilLocation.getKey().getBlock().setType(Material.AIR);
                            SoundManager.playSound(anvilLocation.getKey(), Sound.BLOCK_ANVIL_DESTROY, 100f, 100f);
                            anvilsDamage.remove(anvilLocation.getKey());
                            anvilsLocations.remove(anvilLocation.getKey());
                        }
                    } else {
                        anvilsDamage.replace(anvilLocation.getKey(), anvilDamage + anvilDamage);
                        SoundManager.playSound(anvilLocation.getKey(), Sound.BLOCK_ANVIL_USE, 100f, 100f);
                    }
                    break;
                }
            }
        }
    }
}
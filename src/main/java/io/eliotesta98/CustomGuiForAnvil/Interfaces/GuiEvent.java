package io.eliotesta98.CustomGuiForAnvil.Interfaces;

import de.tr7zw.nbtapi.NBTItem;
import io.eliotesta98.CustomGuiForAnvil.Core.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import java.util.ArrayList;

public class GuiEvent implements Listener {

    private final ArrayList<Player> clicked = new ArrayList<Player>();
    private final boolean debugGui = Main.instance.getConfigGestion().getDebug().get("ClickGui");

    @EventHandler
    public void inventoryDragEvent(final InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof CustomGuiForAnvil) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent inventoryCloseEvent) {
        try {
            if (inventoryCloseEvent.getPlayer() instanceof Player) {
                this.clicked.remove(inventoryCloseEvent.getPlayer());
                if (inventoryCloseEvent.getInventory().getHolder() instanceof CustomGuiForAnvil) {
                    int count = 0;
                    NBTItem nbtItem = null;
                    while (count < inventoryCloseEvent.getInventory().getSize()) {
                        if (inventoryCloseEvent.getInventory().getItem(count) != null) {
                            nbtItem = new NBTItem(inventoryCloseEvent.getInventory().getItem(count));
                            break;
                        }
                        count++;
                    }
                    if (nbtItem != null) {
                        Main.instance.getConfigGestion().getInterfaces().get(nbtItem.getString("cgfa.currentInterface")).removeInventory(inventoryCloseEvent.getPlayer().getName());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void clickInventories(final InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null) {
            final Inventory topInventory = event.getWhoClicked().getOpenInventory().getTopInventory();
            final InventoryAction actionPerformed = event.getAction();
            if (topInventory.getHolder() instanceof CustomGuiForAnvil) {
                if (clickedInventory.getHolder() instanceof CustomGuiForAnvil) {
                    NBTItem nbtItem = new NBTItem(clickedInventory.getContents()[event.getSlot()]);
                    if(nbtItem.getString("cgfa.currentInterface").equalsIgnoreCase("Anvil")) {
                        event.setCancelled(true);
                    }

                    for (int i = 0; i < clickedInventory.getContents().length; i++) {
                        if (clickedInventory.getContents()[i] != null) {

                        } else {
                            System.out.println("EMPTY SLOT");
                        }
                    }
                } else {
                    System.out.println("Player Inventory");
                }
            }
        }
    }

    /*@EventHandler
    public void onClick(final InventoryClickEvent inventoryClickEvent) {
        DebugUtils debug = new DebugUtils();
        long tempo = System.currentTimeMillis();
        if (inventoryClickEvent.getWhoClicked() instanceof Player) {
            final Player player = (Player) inventoryClickEvent.getWhoClicked();
            final Inventory inv = inventoryClickEvent.getClickedInventory();
            clicked.add(player);
            if (inv == null || !inv.getType().equals(InventoryType.CHEST)) {
                if (debugGui) {
                    debug.addLine("Gui execution time= " + (System.currentTimeMillis() - tempo));
                    debug.debug("Gui");
                }
                return;
            }
            if (inv.getHolder() instanceof CustomGuiForAnvil) {
                if (inv.getItem(inventoryClickEvent.getSlot()) == null
                        || inv.getItem(inventoryClickEvent.getSlot()).getType() == Material.AIR) {
                    // se lo slot che clicco è vuoto o aria
                    if (debugGui) {
                        debug.addLine("Gui execution time= " + (System.currentTimeMillis() - tempo));
                        debug.debug("Gui");
                    }
                    return;
                }
                // prendo l'nbtItem
                NBTItem nbtItem = new NBTItem(inv.getItem(inventoryClickEvent.getSlot()));
                String typeInterface = nbtItem.getString("cgfa.currentInterface");
                ArrayList<String> slots = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getSlots();
                String nameItemConfig = Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getItemsConfig().get(slots.get(inventoryClickEvent.getSlot())).getNameItemConfig();
                if (typeInterface.equalsIgnoreCase("Anvil")) {
                    if (Main.instance.getConfigGestion().getInterfaces().get(typeInterface).getItemsConfig().get(slots.get(inventoryClickEvent.getSlot())).getType().equalsIgnoreCase(inventoryClickEvent.getCurrentItem().getType().toString())) {
                        if (nameItemConfig.equalsIgnoreCase("Border")) {
                            inventoryClickEvent.setCancelled(true);
                        } else if (nameItemConfig.equalsIgnoreCase("Item")) {
                            System.out.println("Item");
                        } else if (nameItemConfig.equalsIgnoreCase("Enchant")) {
                            System.out.println("Enchant");
                        } else if (nameItemConfig.equalsIgnoreCase("Result")) {
                            System.out.println("Result");
                            player.closeInventory();
                        } else {
                            System.out.println("Other");
                            inventoryClickEvent.setCancelled(true);
                        }
                    }
                }
            }
        }
    }*/
}
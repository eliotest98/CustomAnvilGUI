package io.eliotesta98.CustomGuiForAnvil.Interfaces;

import io.eliotesta98.CustomGuiForAnvil.Core.Main;
import io.eliotesta98.CustomGuiForAnvil.Utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.Map;

public class _AnvilInterfaceManagement implements Listener {

    public static Map<String, InventoryView> anvilInventories = new HashMap<>();

    @EventHandler
    public void onBlockClick(org.bukkit.event.inventory.InventoryOpenEvent event) {
        System.out.println(event.getInventory().getType());
        if (event.getInventory().getType().toString().equalsIgnoreCase("ANVIL")) {
            openInterface((Player) event.getPlayer(), event.getView());
        }
    }

    public static void openInterface(Player player, InventoryView anvil) {
        anvilInventories.put(player.getName(), anvil);

        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            Inventory customAnvilInventory = getCustomAnvilInventory();
            player.openInventory(customAnvilInventory);
            System.out.println("APerta!");
        }, 100);
    }

    // TODO
    // 1- Drop items in slots
    // 2- On click on specific Item or close (Configurable) of the custom Inventory, open the default anvil inventory
    // 3- Check if the anvil block still exists, in case it doesn't, close the custom inventory immediately!




    public static Inventory getCustomAnvilInventory() {
        Interface _interface = Main.instance.getConfigGestion().getInterfaces().get("Anvil");
        CustomGuiForAnvil holder = new CustomGuiForAnvil(_interface.getSlots().size(), ColorUtils.applyColor(_interface.getTitle()));
        // prendo l'inventario
        final Inventory inventory = holder.getInventory();

        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            for (int i = 0; i < _interface.getSlots().size(); i++) {// scorro gli slot
                String slot = _interface.getSlots().get(i);// prendo lo slot
                if (_interface.getItemsConfig().get(slot) == null) {
                    continue;
                }
                inventory.setItem(i, _interface.getItemsConfig().get(slot).createItemConfig(_interface.getNameInterface(), "", 0));
            }
        });

        return inventory;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof CustomGuiForAnvil) {
            anvilInventories.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
            anvilInventories.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKickEvent(PlayerKickEvent event) {
            anvilInventories.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onInventoryItemMove(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof CustomGuiForAnvil) {
            // Check if the inventory has a single slot empty
            int size = event.getInventory().getSize();
            int count = 0;
            for (int i = 0; i < size; i++) {
                if (event.getInventory().getItem(i) == null) {
                    count++;
                }
            }

            if (count == 1) {
                Player p = (Player) event.getWhoClicked();
                InventoryView asd = anvilInventories.get(p.getName());

                asd.getTopInventory().setItem(0, event.getInventory().getItem(10));
                asd.getTopInventory().setItem(1, event.getInventory().getItem(12));
            }

        }
    }

    @EventHandler
    public void onInventoryPrepareAnvilEvent(org.bukkit.event.inventory.PrepareAnvilEvent event) {
        // Result => event.getResult();
        AnvilInventory anvil = event.getInventory();
        System.out.println("Cost: " + anvil.getRepairCost());
        System.out.println("Cost Amount: " + anvil.getRepairCostAmount());
        System.out.println("Cost Max: " + anvil.getMaximumRepairCost());
        System.out.println("Name: " + event.getView().getPlayer().getName());

        // ((Player)event.getView().getPlayer()).getInventory().firstEmpty();
        // Bukkit.getServer().getPluginManager().callEvent(new InventoryDragEvent(event.getView(), event.getView().getTopInventory()));


        System.out.println(event.getEventName() + " " + event.getResult() + " " + event.getView().getTopInventory().getItem(0) + " " + event.getView().getTopInventory().getItem(1));
        event.getView().getPlayer().getOpenInventory().getTopInventory().setItem(15, event.getResult());
    }

    public static int firstSlotEmpty(Player p) {
        return p.getInventory().firstEmpty();
    }

    public static class MyInventoryView extends InventoryView {
        Player player;
        Inventory topInventory;

        String title = "Anvil Custom";
        String Original;

        public MyInventoryView(Player p) {
            this.player = p;
            this.topInventory = Bukkit.createInventory(player, InventoryType.ANVIL, title);
            this.Original = title;
        }

        @Override
        public Inventory getTopInventory() {
            return topInventory;
        }

        @Override
        public Inventory getBottomInventory() {
            return player.getInventory();
        }

        @Override
        public HumanEntity getPlayer() {
            return player;
        }

        @Override
        public InventoryType getType() {
            return InventoryType.ANVIL;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getOriginalTitle() {
            return Original;
        }

        @Override
        public void setTitle(String s) {
            title = s;
        }
    }
}

package io.eliotesta98.CustomAnvilGUI.Api;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.eliotesta98.CustomAnvilGUI.Core.Main;
import io.eliotesta98.CustomAnvilGUI.Interfaces.Interface;
import io.eliotesta98.CustomAnvilGUI.Utils.DebugUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * CustomAnvilGui API.
 *
 * @author eliotesta98
 * @version 1.0
 */
public class CustomAnvilGuiApi {

    private static final boolean isDebugEnabled = Main.instance.getConfigGestion().getDebug().get("API");

    /**
     * Set the result item in the interface.
     * P. S. Without NbtApi, this method does not work!
     *
     * @param topInventory is the anvil inventory.
     * @param levels       is the cost of enchanting.
     * @param resultItem   is the ItemStack you want a set.
     */
    public static void setResult(Inventory topInventory, int levels, ItemStack resultItem) {
        DebugUtils debugUtils = new DebugUtils();
        if (isDebugEnabled) {
            debugUtils.addLine("setResult api method invocation");
            debugUtils.addLine("Repair cost in levels: " + levels);
            debugUtils.addLine("ItemStack to enchant:" + resultItem);
        }
        Interface interface_ = Main.instance.getConfigGestion().getInterfaces().get("Anvil");
        interface_.setCostOfEnchant(topInventory, levels);

        NBTItem nbtItem = new NBTItem(resultItem);
        nbtItem.setInteger("ap.repairCost", levels);
        topInventory.setItem(interface_.getImportantSlots().get("NoResult"), nbtItem.getItem());
        if (isDebugEnabled) {
            debugUtils.debug("setResult");
        }
    }

}

package io.eliotesta98.CustomAnvilGUI.Module.ExcellentEnchants;

import io.eliotesta98.CustomAnvilGUI.Core.Main;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import su.nightexpress.excellentenchants.api.enchantment.CustomEnchantment;
import su.nightexpress.excellentenchants.EnchantsUtils;

import java.util.Map;

public class ExcellentEnchantsUtils {

    private static boolean excellentEnchants = Main.instance.getConfigGestion().getHooks().get("ExcellentEnchants");

    public static void setExcellentEnchants(boolean excellentEnchants) {
        ExcellentEnchantsUtils.excellentEnchants = excellentEnchants;
    }

    private static Map<CustomEnchantment, Integer> getEnchants(ItemStack itemStack) {
        return EnchantsUtils.getCustomEnchantments(itemStack);
    }

    public static boolean controlEnchant(ItemStack itemStack, String enchantToControl) {
        if (!excellentEnchants) {
            return false;
        }
        if (itemStack == null) {
            return false;
        }
        for (Map.Entry<CustomEnchantment, Integer> enchant : getEnchants(itemStack).entrySet()) {
            if (enchant.getKey().getId().equalsIgnoreCase(enchantToControl)) {
                if (EnchantsUtils.getLevel(itemStack, enchant.getKey().getBukkitEnchantment()) > 0) {
                    if (!enchant.getKey().isOutOfCharges(itemStack)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getRepairCost(ItemStack itemStack) {
        int repairCost = -1;
        if (!excellentEnchants) {
            return repairCost;
        }
        if (itemStack == null) {
            return repairCost;
        }
        ItemMeta meta = itemStack.getItemMeta();
        Main.messageGesturePaper.logDebug("ExcellentEnchants Meta: " + meta);
        if (meta instanceof Repairable) {
            Repairable repairable = (Repairable) meta;
            repairCost = repairable.getRepairCost();
            Main.messageGesturePaper.logDebug("ExcellentEnchants Repair cost: " + repairCost);
        }
        return repairCost;
    }
}

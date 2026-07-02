package io.eliotesta98.CustomAnvilGUI.Module.ExcellentEnchants;

import org.bukkit.inventory.ItemStack;
import su.nightexpress.excellentenchants.api.enchantment.CustomEnchantment;
import su.nightexpress.excellentenchants.EnchantsUtils;

import java.util.Map;

public class ExcellentEnchantsUtils {

    private static Map<CustomEnchantment, Integer> getEnchants(ItemStack itemStack) {
        return EnchantsUtils.getCustomEnchantments(itemStack);
    }

    public static boolean controlEnchant(ItemStack itemStack, String enchantToControl) {
        if (itemStack == null) {
            return false;
        }
        for (Map.Entry<CustomEnchantment, Integer> enchant : getEnchants(itemStack).entrySet()) {
            enchant.getKey().getCharges();
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

}

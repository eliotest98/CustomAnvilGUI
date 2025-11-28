package io.eliotesta98.CustomAnvilGUI.Module.AdvancedEnchantments;

public class AEUtils {

    /*public static ItemStack getResult(ItemStack... items) {
        ItemStack tool = items[0];
        String enchant = getBookEnchant(items[1]);
        int level = getBookLevel(items[1]);
        if (enchant.equalsIgnoreCase("No Book") || level == -1) {
            tool = items[1];
            enchant = getBookEnchant(items[0]);
            level = getBookLevel(items[0]);
        }
        if (enchant.equalsIgnoreCase("No Book") || level == -1) {
            return null;
        }
        return AEAPI.applyEnchant(enchant, level, false, false, tool);
    }

    public static String getBookEnchant(ItemStack book) {
        if (!book.getType().toString().contains("ENCHANTED_BOOK")) {
            return "No Book";
        }
        return AEAPI.getBookEnchantment(book);
    }

    public static int getBookLevel(ItemStack book) {
        if (!book.getType().toString().contains("ENCHANTED_BOOK")) {
            return -1;
        }
        return AEAPI.getBookEnchantmentLevel(book);
    }

    private int calculateCost(int currentCost, int baseRepairCost, int rarityMultiplier, int level, int maxLevel, boolean isBook, ItemStack left) {
        if (isBook) rarityMultiplier = Math.max(1, rarityMultiplier / 2);
        currentCost += rarityMultiplier * Math.min(level, maxLevel);
        if (left.getAmount() > 1) currentCost = baseRepairCost - 1;
        return currentCost;
    }*/

}

package io.eliotesta98.CustomAnvilGUI.Database.Objects;

import com.HeroxWar.HeroxCore.MessageGesture;
import io.eliotesta98.CustomAnvilGUI.Module.Vault.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.script.ScriptException;

public class PaymentConfig {

    private boolean enabled;
    private double price;
    private String type;
    private String calculation = "";
    private String notEnoughMoney = "";
    private String notEnoughMaterial = "";
    private String notEnoughExperience = "";
    private String bypassPermission = "";
    private boolean vaultEnabled = false;

    public PaymentConfig(boolean enabled, double price, String type, String calculation,
                         String messageNotEnoughMoney, String messageNotEnoughMaterial,
                         String messageNotEnoughExperience, boolean vaultEnabled, String bypassPermission) {
        this.enabled = enabled;
        this.price = price;
        this.type = type;
        this.calculation = calculation;
        this.notEnoughExperience = messageNotEnoughExperience;
        this.notEnoughMaterial = messageNotEnoughMaterial;
        this.notEnoughMoney = messageNotEnoughMoney;
        this.vaultEnabled = vaultEnabled;
        this.bypassPermission = bypassPermission;
    }

    public PaymentConfig(boolean enabled, double price, String type,
                         String messageNotEnoughMoney, String messageNotEnoughMaterial,
                         String messageNotEnoughExperience, boolean vaultEnabled, String bypassPermission) {
        this.enabled = enabled;
        this.price = price;
        this.type = type;
        this.notEnoughExperience = messageNotEnoughExperience;
        this.notEnoughMaterial = messageNotEnoughMaterial;
        this.notEnoughMoney = messageNotEnoughMoney;
        this.vaultEnabled = vaultEnabled;
        this.bypassPermission = bypassPermission;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCalculation() {
        return calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    // true = hand
    // false = inventory
    public double calculatePrice(Player player, boolean handOrInventory) {
        if (handOrInventory) {
            ItemStack itemInHand = player.getItemInHand();
            if (itemInHand == null) {
                return 0.0;
            } else if (itemInHand.getType() == Material.AIR) {
                return 0.0;
            } else {
                return placeholderPrice(itemInHand.getDurability(), 1);
            }
        } else {
            double price = 0.0;
            for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                if (itemStack != null && itemStack.getType() != Material.AIR) {
                    price = price + placeholderPrice(itemStack.getDurability(), 1);
                }
            }
            return price;
        }
    }

    public double placeholderPrice(double... informations) {
        double price = 0.0;
        try {
            price = calculateFunction(
                    calculation
                            .replace("{durability}", "" + informations[0])
                            .replace("{numberOfItems}", "" + informations[1])
                            .replace("{price}", "" + this.price)
            );
        } catch (ScriptException e) {
            return price;
        }
        return price;
    }

    public boolean pay(Player p, double price) {
        if (p.hasPermission(bypassPermission)) {
            return true;
        }
        if (price <= 0.0) {
            price = this.price;
        }
        // Payment
        if (enabled) {
            if (type.equalsIgnoreCase("Money")) {
                if (vaultEnabled) {
                    if (!VaultUtils.pay(p, price)) {
                        MessageGesture.sendMessage(p, notEnoughMoney);
                        return false;
                    }
                } else {
                    MessageGesture.sendMessage(Bukkit.getServer().getConsoleSender(), "&c&lERROR WITH CONFIGURATION AT Configuration.BreakGesture.Payment.Type, IF YOU WANT THIS PAYMENT TYPE PLEASE ENABLE VAULT COMPATIBILITY!");
                }
            } else if (type.equalsIgnoreCase("Experience")) {
                int levelPayment = (int) price;
                int finalLevel = p.getLevel() - levelPayment;
                if (finalLevel >= 0) {
                    p.setLevel(finalLevel);
                } else {
                    MessageGesture.sendMessage(p, notEnoughExperience);
                    return false;
                }
            } else {
                String[] material = type.split(":");
                Material realMaterial = Material.getMaterial(material[1]);
                ItemStack itemStack = new ItemStack(realMaterial, (int) price);
                if (material.length == 3) {
                    itemStack = new ItemStack(realMaterial, (int) price, Short.parseShort(material[2]));
                }
                if (!p.getInventory().contains(itemStack.getType(), (int) price)) {
                    MessageGesture.sendMessage(p, notEnoughMaterial);
                    return false;
                } else {
                    int itemSlot = p.getInventory().first(itemStack.getType());
                    itemStack = p.getInventory().getItem(itemSlot);
                    p.getInventory().getItem(itemSlot).setAmount((int) (itemStack.getAmount() - price));
                }
            }
            return true;
        }
        return true;
    }

    // 0 = durability
    // 1 = numberOfItems
    public boolean customPay(Player p, double... informations) {
        try {
            double price = calculateFunction(
                    calculation
                            .replace("{durability}", "" + informations[0])
                            .replace("{numberOfItems}", "" + informations[1])
                            .replace("{price}", "" + this.price)
            );
            boolean payed = pay(p, price);
            if (!payed) {
                return false;
            }
        } catch (ScriptException e) {
            MessageGesture.sendMessage(p, "&cAn Internal Error Occurred with function for calculate the price of tp");
            return false;
        }
        return true;
    }

    private double calculateFunction(String expression) throws ScriptException {
        String[] operation;
        double calculate = -1.0;
        if (expression.contains("*")) {
            operation = expression.split("\\*");
            calculate = Double.parseDouble(operation[0]);
            for (int i = 1; i < operation.length; i++) {
                calculate = calculate * Double.parseDouble(operation[i]);
            }
        } else if (expression.contains("/")) {
            operation = expression.split("/");
            calculate = Double.parseDouble(operation[0]);
            for (int i = 1; i < operation.length; i++) {
                calculate = calculate / Double.parseDouble(operation[i]);
            }
        } else if (expression.contains("+")) {
            operation = expression.split("\\+");
            calculate = Double.parseDouble(operation[0]);
            for (int i = 1; i < operation.length; i++) {
                calculate = calculate + Double.parseDouble(operation[i]);
            }
        } else if (expression.contains("-")) {
            operation = expression.split("-");
            calculate = Double.parseDouble(operation[0]);
            for (int i = 1; i < operation.length; i++) {
                calculate = calculate - Double.parseDouble(operation[i]);
            }
        }
        return calculate;
    }
}

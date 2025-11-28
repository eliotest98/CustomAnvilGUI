package io.eliotesta98.CustomAnvilGUI.Module.Vault;

import io.eliotesta98.CustomAnvilGUI.Core.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultUtils {

    private static Economy economy;

    public static boolean setupEconomy() {
        final RegisteredServiceProvider<Economy> registration = Main.instance.getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (registration == null) {
            return false;
        }
        economy = registration.getProvider();
        return true;
    }

    // Deposit Money at Player
    @SuppressWarnings("deprecation")
    public void depositMoney(Player player, double n) {
        EconomyResponse economyResponse = economy.depositPlayer(player.getName(), n);
        if (!economyResponse.transactionSuccess()) {
            System.out.print("An error occured while depositing money.");
        }
    }

    // Get Money from Player
    @SuppressWarnings("deprecation")
    public static boolean pay(Player player, double n) {
        double balance = economy.getBalance(player);
        double sottrazione = balance - n;
        if (sottrazione < 0.0) {
            return false;
        } else {
            economy.withdrawPlayer(player.getName(), n);
            return true;
        }
    }
}

package io.eliotesta98.CustomAnvilGUI.Core;

import com.HeroxWar.HeroxCore.MainCommons;
import com.HeroxWar.HeroxCore.MessageGesture.MessageGesturePaper;
import com.HeroxWar.HeroxCore.Utils.UpdateChecker;
import com.HeroxWar.HeroxCore.Utils.Version;
import io.eliotesta98.CustomAnvilGUI.Commands.AnvilCommand;
import io.eliotesta98.CustomAnvilGUI.Commands.Commands;
import io.eliotesta98.CustomAnvilGUI.Commands.TabCommands;
import io.eliotesta98.CustomAnvilGUI.Database.ConfigGestion;
import io.eliotesta98.CustomAnvilGUI.Interfaces.GuiEvent;
import io.eliotesta98.CustomAnvilGUI.Interfaces.Interface;
import io.eliotesta98.CustomAnvilGUI.Module.ExcellentEnchants.ExcellentEnchantsUtils;
import io.eliotesta98.CustomAnvilGUI.Module.Floodgate.FloodgateUtils;
import io.eliotesta98.CustomAnvilGUI.Module.Vault.VaultUtils;
import org.bukkit.Bukkit;
import java.io.*;
import java.util.Map;

public class Main extends MainCommons {

    public static Main instance;
    public static FloodgateUtils floodgateUtils;
    private ConfigGestion config;
    public static MessageGesturePaper messageGesturePaper;
    public static Version version;

    private AnvilCommand anvilCommand;

    @Override
    public void onLoad() {
        instance = this;
        onLoadInit(this);
        floodgateUtils = new FloodgateUtils();
    }

    public void onEnable() {
        onEnableInit(this, "\r\n \r\n \r\n &a #####      #      #####   #     #  ### \n" +
                "&a #     #    # #    #     #  #     #   #  \n" +
                "&a #         #   #   #        #     #   #  \n" +
                "&a #        #     #  #  ####  #     #   #  \n" +
                "&a #        #######  #     #  #     #   #  \n" +
                "&a #     #  #     #  #     #  #     #   #  \n" +
                "&a  #####   #     #   #####    #####   ### " +
                "                                   \n\n", 25649);

        messageGesturePaper = getMessageGesturePaper();
        version = getVersion();

        loadConfigs();
    }

    public void onDisable() {
        messageGesturePaper.sendMessage("&aCustomAnvilGUI has been disabled, &cBye bye! §e:(");
        unload();
    }

    public void loadConfigs() {
        messageGesturePaper.sendMessage("&6Loading config...");

        config = new ConfigGestion(this.getDataFolder().getPath(), "config.yml",
                "Configuration.Auto_selling.Timer",
                "Configuration.Prices");

        messageGesturePaper.setPrefix(config.getMessages().get("Prefix").trim());

        messageGesturePaper.sendMessage("&aConfiguration Loaded!");

        new UpdateChecker(instance, 116411).getVersion(version1 -> {
            if (!instance.getDescription().getVersion().equals(version1)) {
                messageGesturePaper.sendMessage("&cNew Update available for CustomAnvilGUI!");
            }
        });

        // RUNNABLE PER CARICARE LE DIPENDENZE ALLA FINE DELL'AVVIO DEL SERVER :D
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (config.getHooks().get("Floodgate")) {
                try {
                    floodgateUtils.initialize();
                    messageGesturePaper.sendMessage("&7Added compatibility with Floodgate.");
                } catch (Exception e) {
                    messageGesturePaper.sendMessage("&cSomething went wrong while adding compatibility to &eFloodgate&c! &f" + e.getMessage());
                }
            } else {
                config.getHooks().replace("Floodgate", false);
            }
            /*if (config.getHooks().get("AdvancedEnchantments")) {
                if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
                    MessageGesture.sendMessage(Main.instance.getServer().getConsoleSender(), "&fAdvancedEnchantments&a hooked!");
                } else {
                    config.getHooks().replace("AdvancedEnchantments", false);
                }
            }*/
            if (getServer().getPluginManager().isPluginEnabled("Vault")) {
                if (config.getHooks().get("Vault")) {
                    if (VaultUtils.setupEconomy()) {
                        messageGesturePaper.sendMessage("&7Added compatibility with Vault.");
                    }
                }
            } else {
                config.getHooks().replace("Vault", false);
            }
            if (getServer().getPluginManager().isPluginEnabled("ExcellentEnchants")) {
                if (config.getHooks().get("ExcellentEnchants")) {
                    ExcellentEnchantsUtils.setExcellentEnchants(true);
                    messageGesturePaper.sendMessage("&7Added compatibility with ExcellentEnchants.");
                }
            } else {
                config.getHooks().replace("ExcellentEnchants", false);
            }
            if(getServer().getPluginManager().isPluginEnabled("Essentials")) {
                if (config.getHooks().get("Essentials")) {
                    messageGesturePaper.sendMessage("&7Added compatibility with Essentials.");
                }
            } else {
                config.getHooks().replace("Essentials", false);
            }
        });

        if(getServer().getPluginManager().isPluginEnabled("Essentials")) {
            if (config.getHooks().get("Essentials")) {
                config.setVirtualAnvilEnabled(true);
            }
        }

        GuiEvent guiEvent = new GuiEvent();
        Bukkit.getServer().getPluginManager().registerEvents(guiEvent, this);
        if (config.isVirtualAnvilEnabled()) {
            anvilCommand = new AnvilCommand(guiEvent);
            Bukkit.getServer().getPluginManager().registerEvents(anvilCommand, this);
        }
        getCommand("customanvilgui").setExecutor(new Commands());
        getCommand("customanvilgui").setTabCompleter(new TabCommands());
    }

    public void unload() {
        for (Map.Entry<String, Interface> inventory : config.getInterfaces().entrySet()) {
            inventory.getValue().closeAllInventories();
        }
        if (config.isVirtualAnvilEnabled() && anvilCommand != null) {
            anvilCommand.disableEvent();
        }
    }

    public ConfigGestion getConfigGestion() {
        return config;
    }

    public void setConfigGestion(ConfigGestion config) {
        this.config = config;
    }

}

package io.eliotesta98.CustomAnvilGUI.Core;

import com.HeroxWar.HeroxCore.MessageGesture.MessageGesturePaper;
import com.HeroxWar.HeroxCore.Utils.Library;
import com.HeroxWar.HeroxCore.Utils.Metrics;
import com.HeroxWar.HeroxCore.Utils.UpdateChecker;
import com.HeroxWar.HeroxCore.Utils.Version;
import io.eliotesta98.CustomAnvilGUI.Commands.Commands;
import io.eliotesta98.CustomAnvilGUI.Commands.TabCommands;
import io.eliotesta98.CustomAnvilGUI.Database.ConfigGestion;
import io.eliotesta98.CustomAnvilGUI.Interfaces.GuiEvent;
import io.eliotesta98.CustomAnvilGUI.Interfaces.Interface;
import io.eliotesta98.CustomAnvilGUI.Module.Floodgate.FloodgateUtils;
import io.eliotesta98.CustomAnvilGUI.Module.Vault.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin {

    public static Main instance;
    public static FloodgateUtils floodgateUtils;
    private ConfigGestion config;
    public static MessageGesturePaper messageGesturePaper;
    private List<String> libraryLegacyMessages = new ArrayList<>();
    public static Version version;

    @Override
    public void onLoad() {
        instance = this;
        floodgateUtils = new FloodgateUtils();
        version = new Version();
        // Load libraries where Spigot does not do this automatically
        libraryLegacyMessages = loadLibraries();
    }

    public void onEnable() {
        int pluginId = 25649;
        new Metrics(this, pluginId);

        messageGesturePaper = new MessageGesturePaper(true, false, instance);

        for(String message: libraryLegacyMessages) {
            messageGesturePaper.sendMessage(message);
        }
        libraryLegacyMessages.clear();

        messageGesturePaper
                .sendMessage("\r\n \r\n \r\n &a #####      #      #####   #     #  ### \n" +
                        "&a #     #    # #    #     #  #     #   #  \n" +
                        "&a #         #   #   #        #     #   #  \n" +
                        "&a #        #     #  #  ####  #     #   #  \n" +
                        "&a #        #######  #     #  #     #   #  \n" +
                        "&a #     #  #     #  #     #  #     #   #  \n" +
                        "&a  #####   #     #   #####    #####   ### " +
                        "                                   \n\n"
                        + "&e  Version " + getDescription().getVersion() + " \r\n"
                        + "&e© Developed by &feliotesta98 & xSavior_of_God &ewith &4<3 \r\n \r\n");

        if (version.isInRange(8, 12)) {
            messageGesturePaper.sendMessage("&6Server version registered < 1.13");
        } else {
            messageGesturePaper.sendMessage("&6Server version registered > 1.12");
        }
        messageGesturePaper.sendMessage("Version Detected: &c" + version.getFormattedServerVersion());

        messageGesturePaper.sendMessage("&6Loading config...");

        config = new ConfigGestion(this.getDataFolder().getPath(), "config.yml",
                "Configuration.Auto_selling.Timer",
                "Configuration.Prices");
        messageGesturePaper.sendMessage("&aConfiguration Loaded!");

        new UpdateChecker(instance, 116411).getVersion(version1 -> {
            if (!instance.getDescription().getVersion().equals(version1)) {
                messageGesturePaper.sendMessage("&cNew Update available for CustomAnvilGUI!");
            }
        });

        // RUNNABLE PER CARICARE LE DIPENDENZE ALLA FINE DELL'AVVIO DEL SERVER :D
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (getConfigGestion().getHooks().get("Floodgate")) {
                try {
                    floodgateUtils.initialize();
                    messageGesturePaper.sendMessage("&7Added compatibility with Floodgate.");
                } catch (Exception e) {
                    messageGesturePaper.sendMessage("&cSomething went wrong while adding compatibility to &eFloodgate&c! &f" + e.getMessage());
                }
            }
            /*if (getConfigGestion().getHooks().get("AdvancedEnchantments")) {
                if (Bukkit.getPluginManager().isPluginEnabled("AdvancedEnchantments")) {
                    MessageGesture.sendMessage(Main.instance.getServer().getConsoleSender(), "&fAdvancedEnchantments&a hooked!");
                } else {
                    getConfigGestion().getHooks().replace("AdvancedEnchantments", false);
                }
            }*/
            if (getServer().getPluginManager().isPluginEnabled("Vault")) {
                if (getConfigGestion().getHooks().get("Vault")) {
                    if (VaultUtils.setupEconomy()) {
                        messageGesturePaper.sendMessage("&7Added compatibility with Vault.");
                    }
                }
            } else {
                getConfigGestion().getHooks().replace("Vault", false);
            }
        });

        Bukkit.getServer().getPluginManager().registerEvents(new GuiEvent(), this);
        getCommand("customanvilgui").setExecutor(new Commands());
        getCommand("customanvilgui").setTabCompleter(new TabCommands());
    }

    public void onDisable() {
        messageGesturePaper.sendMessage("&aCustomAnvilGUI has been disabled, &cBye bye! §e:(");
        for (Map.Entry<String, Interface> inventory : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
            inventory.getValue().closeAllInventories();
        }
    }

    public ConfigGestion getConfigGestion() {
        return config;
    }

    private List<String> loadLibraries() {
        final List<Library> libraries = new ArrayList<>();

        boolean oldVersion = version.isInRange(8, 16);

        List<String> messagesToSend = new ArrayList<>();

        if (oldVersion) {
            messagesToSend.add("Loading legacy libraries...");
            Reader targetReader = new InputStreamReader(getResource("plugin.yml"));

            YamlConfiguration pluginFile = YamlConfiguration.loadConfiguration(targetReader);
            for (final String libraryPath : pluginFile.getStringList("legacy-libraries")) {
                final Library library = Library.fromMavenRepo(libraryPath);
                messagesToSend.add("Loading library " + libraryPath);
                libraries.add(library);
            }

            for (final Library library : libraries)
                library.load(Main.class.getClassLoader());
            messagesToSend.add("Legacy libraries loaded!");
        }
        return messagesToSend;
    }

}

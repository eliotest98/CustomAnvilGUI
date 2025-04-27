package io.eliotesta98.CustomAnvilGUI.Core;

import io.eliotesta98.CustomAnvilGUI.Commands.Commands;
import io.eliotesta98.CustomAnvilGUI.Database.ConfigGestion;
import io.eliotesta98.CustomAnvilGUI.Interfaces.GuiEvent;
import io.eliotesta98.CustomAnvilGUI.Interfaces.Interface;
import io.eliotesta98.CustomAnvilGUI.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin {

    public static Main instance;
    private ConfigGestion config;
    public SoundManager soundManager;

    @Override
    public void onLoad() {
        instance = this;

        // Load libraries where Spigot does not do this automatically
        //loadLibraries();
    }

    public void onEnable() {
        DebugUtils debugsistem = new DebugUtils();
        long tempo = System.currentTimeMillis();
        int pluginId = 25649;

        getServer().getConsoleSender()
                .sendMessage("\r\n \r\n \r\n §a #####      #      #####   #     #  ### \n" +
                        "§a #     #    # #    #     #  #     #   #  \n" +
                        "§a #         #   #   #        #     #   #  \n" +
                        "§a #        #     #  #  ####  #     #   #  \n" +
                        "§a #        #######  #     #  #     #   #  \n" +
                        "§a #     #  #     #  #     #  #     #   #  \n" +
                        "§a  #####   #     #   #####    #####   ### " +
                        "                                   \n\n"
                        + "§e  Version " + getDescription().getVersion() + " \r\n"
                        + "§e© Developed by §feliotesta98 & xSavior_of_God §ewith §4<3 \r\n \r\n");

        new Metrics(this, pluginId);
        this.getServer().getConsoleSender().sendMessage("§6Loading config...");
        this.soundManager = new SoundManager();
        File configFile = new File(this.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {

                this.saveResource("config.yml", false);
                inputStream = this.getResource("config.yml");

                // write the inputStream to a FileOutputStream
                outputStream = new FileOutputStream(configFile);

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create config.yml!");
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        // outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        CommentedConfiguration cfg = CommentedConfiguration.loadConfiguration(configFile);

        try {
            String configname;

            configname = "config.yml";

            String splits = "Configuration.Auto_selling.Timer:Configuration.Prices";
            String[] strings = splits.split(":");
            cfg.syncWithConfig(configFile, this.getResource(configname), strings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        config = new ConfigGestion(YamlConfiguration.loadConfiguration(configFile));
        getServer().getConsoleSender().sendMessage("§aConfiguration Loaded!");

        Bukkit.getServer().getPluginManager().registerEvents(new GuiEvent(), this);
        getCommand("customanvilgui").setExecutor(new Commands());

        if (config.getDebug().get("Enabled")) {
            debugsistem.addLine("Enabled execution time= " + (System.currentTimeMillis() - tempo));
            debugsistem.debug("Enabled");
        }
    }

    public void onDisable() {
        DebugUtils debugsistem = new DebugUtils();
        long tempo = System.currentTimeMillis();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "CustomAnvilGUI has been disabled, §cBye bye! §e:(");
        for (Map.Entry<String, Interface> inventory : Main.instance.getConfigGestion().getInterfaces().entrySet()) {
            inventory.getValue().closeAllInventories();
        }
        if (config.getDebug().get("Disabled")) {
            debugsistem.addLine("Disabled execution time= " + (System.currentTimeMillis() - tempo));
            debugsistem.debug("Disabled");
        }
    }

    public ConfigGestion getConfigGestion() {
        return config;
    }

    private void loadLibraries() {
        final List<Library> libraries = new ArrayList<>();

        boolean oldVersion = getServer().getVersion().contains("1.8") || getServer().getVersion().contains("1.9")
                || getServer().getVersion().contains("1.10") || getServer().getVersion().contains("1.11")
                || getServer().getVersion().contains("1.12") || getServer().getVersion().contains("1.13")
                || getServer().getVersion().contains("1.14") || getServer().getVersion().contains("1.15")
                || getServer().getVersion().contains("1.16");

        if (oldVersion) {
            Bukkit.getConsoleSender().sendMessage("Loading legacy libraries...");
            Reader targetReader = new InputStreamReader(getResource("plugin.yml"));

            YamlConfiguration pluginFile = YamlConfiguration.loadConfiguration(targetReader);
            for (final String libraryPath : pluginFile.getStringList("legacy-libraries")) {
                final Library library = Library.fromMavenRepo(libraryPath);
                Bukkit.getConsoleSender().sendMessage("Loading library " + libraryPath);
                libraries.add(library);
            }

            for (final Library library : libraries)
                library.load();
            Bukkit.getConsoleSender().sendMessage("Legacy libraries loaded!");
        }
    }

}

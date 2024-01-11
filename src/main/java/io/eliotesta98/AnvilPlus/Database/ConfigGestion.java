package io.eliotesta98.AnvilPlus.Database;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigGestion {

    private HashMap<String, Boolean> hooks = new HashMap<>();
    private HashMap<String, String> messages = new HashMap<>();
    private HashMap<String, Boolean> debug = new HashMap<>();

    public ConfigGestion(FileConfiguration file) {

        for (String event : file.getConfigurationSection("Debug").getKeys(false)) {
            debug.put(event, file.getBoolean("Debug." + event));
        }

        for (String hook : file.getConfigurationSection("Configuration.Hooks").getKeys(false)) {
            hooks.put(hook, file.getBoolean("Configuration.Hooks." + hook));
        }

        String prefix = "";
        for (String message : file.getConfigurationSection("Messages").getKeys(false)) {
            if (message.equalsIgnoreCase("Commands")) {
                for (String commands : file.getConfigurationSection("Messages.Commands").getKeys(false)) {
                    messages.put(message + "." + commands, file.getString("Messages.Commands." + commands));
                }
            } else if (message.equalsIgnoreCase("Prefix")) {
                prefix = file.getString("Messages." + message);
                messages.put(message, prefix);
            } else if (message.equalsIgnoreCase("Success")) {
                for (String success : file.getConfigurationSection("Messages.Success").getKeys(false)) {
                    messages.put(message + "." + success, file.getString("Messages.Success." + success).replace("{prefix}", prefix));
                }
            } else if (message.equalsIgnoreCase("Lists")) {
                for (String success : file.getConfigurationSection("Messages.Lists").getKeys(false)) {
                    messages.put(message + "." + success, file.getString("Messages.Lists." + success).replace("{prefix}", prefix));
                }
            } else if (message.equalsIgnoreCase("Warnings")) {
                for (String success : file.getConfigurationSection("Messages.Warnings").getKeys(false)) {
                    messages.put(message + "." + success, file.getString("Messages.Warnings." + success).replace("{prefix}", prefix));
                }
            } else if (message.equalsIgnoreCase("Errors")) {
                for (String success : file.getConfigurationSection("Messages.Errors").getKeys(false)) {
                    messages.put(message + "." + success, file.getString("Messages.Errors." + success).replace("{prefix}", prefix));
                }
            } else {
                messages.put(message, file.getString("Messages." + message).replace("{prefix}", prefix));
            }
        }
    }

    public HashMap<String, Boolean> getHooks() {
        return hooks;
    }

    public void setHooks(HashMap<String, Boolean> hooks) {
        this.hooks = hooks;
    }

    public HashMap<String, String> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, String> messages) {
        this.messages = messages;
    }

    public HashMap<String, Boolean> getDebug() {
        return debug;
    }

    public void setDebug(HashMap<String, Boolean> debug) {
        this.debug = debug;
    }

    @Override
    public String toString() {
        return "ConfigGestion{" +
                "hooks=" + hooks +
                ", messages=" + messages +
                ", debug=" + debug +
                '}';
    }
}

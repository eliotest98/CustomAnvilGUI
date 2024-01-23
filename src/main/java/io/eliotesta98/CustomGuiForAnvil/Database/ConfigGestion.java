package io.eliotesta98.CustomGuiForAnvil.Database;

import io.eliotesta98.CustomGuiForAnvil.Interfaces.Interface;
import io.eliotesta98.CustomGuiForAnvil.Interfaces.ItemConfig;
import io.eliotesta98.CustomGuiForAnvil.Utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfigGestion {

    private HashMap<String, Boolean> hooks = new HashMap<>();
    private HashMap<String, String> messages = new HashMap<>();
    private HashMap<String, Boolean> debug = new HashMap<>();
    private HashMap<String, Interface> interfaces = new HashMap<>();

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

        for (String nameInterface : file.getConfigurationSection("Interface").getKeys(false)) {
            String title = file.getString("Interface." + nameInterface + ".Title");
            String openSound = file.getString("Interface." + nameInterface + ".OpenSound");
            ArrayList<String> slots = new ArrayList<>();
            ArrayList<String> contaSlots = new ArrayList<>();

            HashMap<String, ItemConfig> itemsConfig = new HashMap<>();
            for (String nameItem : file.getConfigurationSection("Interface." + nameInterface + ".Items").getKeys(false)) {
                String letter = file.getString("Interface." + nameInterface + ".Items." + nameItem + ".Letter");
                String type = file.getString("Interface." + nameInterface + ".Items." + nameItem + ".Type", "");
                if (!type.equalsIgnoreCase("")) {
                    if (type.contains(";")) {
                        String[] x = type.split(";");
                        if (Material.getMaterial(x[0]) == null) {
                            Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR WITH MATERIAL " + x[0] + " IN CONFIG.YML AT LINE: Interfaces." + nameInterface + ".Items." + nameItem + ".Type"));
                            type = "DIRT";
                        }
                    } else {
                        if (Material.getMaterial(type) == null) {
                            Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&c&lERROR WITH MATERIAL " + type + " IN CONFIG.YML AT LINE: Interfaces." + nameInterface + ".Items." + nameItem + ".Type"));
                            type = "DIRT";
                        }
                    }
                }
                String name = file.getString("Interface." + nameInterface + ".Items." + nameItem + ".Name");
                String texture = file.getString("Interface." + nameInterface + ".Items." + nameItem + ".Texture");
                String soundClick = file.getString("Interface." + nameInterface + ".Items." + nameItem + ".SoundClick");
                ArrayList<String> lore = new ArrayList<String>(file.getStringList("Interface." + nameInterface + ".Items." + nameItem + ".Lore"));
                ItemConfig item = new ItemConfig(nameItem, name, type, texture, lore, soundClick);
                itemsConfig.put(letter, item);
            }

            file.getStringList("Interface." + nameInterface + ".Slots").forEach(value -> {
                for (int i = 0; i < value.length(); i++) {
                    for (Map.Entry<String, ItemConfig> itemConfig : itemsConfig.entrySet()) {
                        if (itemConfig.getKey().equalsIgnoreCase(value.charAt(i) + "") && itemConfig.getValue().getNameItemConfig().equalsIgnoreCase("Item")) {
                            contaSlots.add("" + value.charAt(i));
                        }
                    }
                    slots.add("" + value.charAt(i));
                }
            });
            Interface customInterface;
            if (nameInterface.equalsIgnoreCase("Anvil")) {
                customInterface = new Interface(title, openSound, slots, itemsConfig, debug.get("ClickGui"),
                        contaSlots.size(), nameInterface, "Generator", "");
            } else {
                customInterface = new Interface(title, openSound, slots, itemsConfig, debug.get("ClickGui"),
                        contaSlots.size(), nameInterface, "", "");
            }
            interfaces.put(nameInterface, customInterface);
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

    public HashMap<String, Interface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(HashMap<String, Interface> interfaces) {
        this.interfaces = interfaces;
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

package io.eliotesta98.CustomAnvilGUI.Database;

import io.eliotesta98.CustomAnvilGUI.Interfaces.FloodgateInput;
import io.eliotesta98.CustomAnvilGUI.Interfaces.Interface;
import io.eliotesta98.CustomAnvilGUI.Interfaces.ItemConfig;
import io.eliotesta98.CustomAnvilGUI.Utils.ColorUtils;
import io.eliotesta98.CustomAnvilGUI.Utils.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigGestion {

    private final HashMap<String, Boolean> hooks = new HashMap<>();
    private final HashMap<String, String> messages = new HashMap<>();
    private final HashMap<String, Boolean> debug = new HashMap<>();
    private final HashMap<String, Interface> interfaces = new HashMap<>();
    private final int percentageDamage;
    private final boolean directRename;

    public ConfigGestion(FileConfiguration file) {

        percentageDamage = file.getInt("Configuration.AnvilDamage.Damage", 12);
        directRename = file.getBoolean("Configuration.DirectRename");

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
            } else if (message.equalsIgnoreCase("Lists")) {
                for (String success : file.getConfigurationSection("Messages.Lists").getKeys(false)) {
                    messages.put(message + "." + success, file.getString("Messages.Lists." + success).replace("{prefix}", prefix));
                }
            } else if (message.equalsIgnoreCase("Warnings")
                    || message.equalsIgnoreCase("Errors")
                    || message.equalsIgnoreCase("Success")
                    || message.equalsIgnoreCase("Info")
                    || message.equalsIgnoreCase("Results")
            ) {
                for (String success : file.getConfigurationSection("Messages." + message).getKeys(false)) {
                    messages.put(message + "." + success, file.getString("Messages." + message + "." + success).replace("{prefix}", prefix));
                }
            } else {
                messages.put(message, file.getString("Messages." + message).replace("{prefix}", prefix));
            }
        }

        for (String nameInterface : file.getConfigurationSection("Interface").getKeys(false)) {
            String title = file.getString("Interface." + nameInterface + ".Title");
            String rawSound = file.getString("Interface." + nameInterface + ".OpenSound", "");
            Sound openSound = SoundManager.getSound(rawSound);
            if (openSound == null && !rawSound.equalsIgnoreCase("")) {
                Bukkit.getConsoleSender().sendMessage(ColorUtils.applyColor("&cERROR UNABLE TO RESOLVE SOUND " + rawSound + " for Interfaces." + nameInterface + ".OpenSound"));
            }
            ArrayList<String> slots = new ArrayList<>();
            ArrayList<String> contaSlots = new ArrayList<>();

            List<FloodgateInput> inputs = new ArrayList<>();
            for (String number : file.getConfigurationSection("Interface." + nameInterface + ".Floodgate").getKeys(false)) {
                String base = "Interface." + nameInterface + ".Floodgate." + number;
                FloodgateInput floodgateInput = new FloodgateInput(
                        file.getString(base + ".Type"),
                        file.getString(base + ".Label"),
                        file.getString(base + ".Placeholder", ""),
                        file.getString(base + ".DefaultText", ""));
                inputs.add(floodgateInput);
            }

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
            Interface customInterface = new Interface(title, openSound, slots, itemsConfig, inputs, debug.get("ClickGui"),
                    contaSlots.size(), nameInterface, "", "");
            customInterface.initialize(messages.get("Success.Rename"), directRename, messages.get("Errors.InsufficientExperience"));
            interfaces.put(nameInterface, customInterface);
        }
    }

    public HashMap<String, Boolean> getHooks() {
        return hooks;
    }

    public HashMap<String, String> getMessages() {
        return messages;
    }

    public HashMap<String, Boolean> getDebug() {
        return debug;
    }

    public HashMap<String, Interface> getInterfaces() {
        return interfaces;
    }

    public int getPercentageDamage() {
        return percentageDamage;
    }

    public boolean isDirectRename() {
        return directRename;
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

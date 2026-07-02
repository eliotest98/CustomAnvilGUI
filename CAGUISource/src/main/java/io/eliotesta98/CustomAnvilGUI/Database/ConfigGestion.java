package io.eliotesta98.CustomAnvilGUI.Database;

import com.HeroxWar.HeroxCore.Gestion.DefaultGestion;
import com.HeroxWar.HeroxCore.SoundGesture.SoundType;
import io.eliotesta98.CustomAnvilGUI.Core.Main;
import io.eliotesta98.CustomAnvilGUI.Database.Objects.PaymentConfig;
import io.eliotesta98.CustomAnvilGUI.Interfaces.FloodgateInput;
import io.eliotesta98.CustomAnvilGUI.Interfaces.Interface;
import io.eliotesta98.CustomAnvilGUI.Interfaces.ItemConfig;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigGestion extends DefaultGestion {

    private final HashMap<String, Interface> interfaces = new HashMap<>();
    private final SoundType stageSound;
    private final int percentageDamage;
    private final boolean directRename, onlyBedrock;
    private final PaymentConfig fixHandPayment, fixInventoryPayment;

    private final FileConfiguration file;

    public ConfigGestion(String path, String fileName, String... ignoredSections) {
        super(path, fileName, Main.instance.getName(), ignoredSections);
        file = this.getFileConfiguration();
        this.defaultInformations();

        percentageDamage = file.getInt("Configuration.AnvilDamage.Damage", 12);
        directRename = file.getBoolean("Configuration.DirectRename");
        onlyBedrock = file.getBoolean("Configuration.OnlyBedrock");
        stageSound = new SoundType(
                file.getString("Configuration.AnvilSound.SoundName"),
                file.getDouble("Configuration.AnvilSound.Volume"),
                file.getDouble("Configuration.AnvilSound.Pitch")
        );

        String messageNotEnoughMaterial = getMessages().get("Errors.NotEnoughMaterial");
        String messageNotEnoughExperience = getMessages().get("Errors.NotEnoughExperience");
        String messageNotEnoughMoney = getMessages().get("Errors.NotEnoughMoney");
        boolean vaultEnable = getHooks().get("Vault");

        fixHandPayment = new PaymentConfig(
                file.getBoolean("Configuration.FixItems.Hand.Payment.Enabled"),
                file.getDouble("Configuration.FixItems.Hand.Payment.Price"),
                file.getString("Configuration.FixItems.Hand.Payment.Type"),
                file.getString("Configuration.FixItems.Hand.Payment.Calculation"),
                messageNotEnoughMoney, messageNotEnoughMaterial,
                messageNotEnoughExperience, vaultEnable, "cagui.fix.hand.bypass");

        fixInventoryPayment = new PaymentConfig(
                file.getBoolean("Configuration.FixItems.Inventory.Payment.Enabled"),
                file.getDouble("Configuration.FixItems.Inventory.Payment.Price"),
                file.getString("Configuration.FixItems.Inventory.Payment.Type"),
                file.getString("Configuration.FixItems.Inventory.Payment.Calculation"),
                messageNotEnoughMoney, messageNotEnoughMaterial,
                messageNotEnoughExperience, vaultEnable, "cagui.fix.inventory.bypass");

        for (String nameInterface : file.getConfigurationSection("Interface").getKeys(false)) {
            String title = file.getString("Interface." + nameInterface + ".Title");
            String rawSound = file.getString("Interface." + nameInterface + ".OpenSound.SoundName", "minecraft:entity.allay.item_thrown");
            double volume = file.getDouble("Interface." + nameInterface + ".OpenSound.SoundName", 100.0);
            double pitch = file.getDouble("Interface." + nameInterface + ".OpenSound.SoundName", 2.0);
            SoundType openSound = new SoundType(rawSound, volume, pitch);
            ArrayList<String> slots = new ArrayList<>();
            ArrayList<String> contaSlots = new ArrayList<>();

            List<FloodgateInput> inputs = new ArrayList<>();
            if (file.isConfigurationSection("Interface." + nameInterface + ".Floodgate")) {
                for (String number : file.getConfigurationSection("Interface." + nameInterface + ".Floodgate").getKeys(false)) {
                    String base = "Interface." + nameInterface + ".Floodgate." + number;
                    FloodgateInput floodgateInput = new FloodgateInput(
                            file.getString(base + ".Type"),
                            file.getString(base + ".Label"),
                            file.getString(base + ".Placeholder", ""),
                            file.getString(base + ".DefaultText", ""));
                    inputs.add(floodgateInput);
                }
            }

            HashMap<String, ItemConfig> itemsConfig = new HashMap<>();
            for (String nameItem : file.getConfigurationSection("Interface." + nameInterface + ".Items").getKeys(false)) {
                String letter = file.getString("Interface." + nameInterface + ".Items." + nameItem + ".Letter");
                String type = file.getString("Interface." + nameInterface + ".Items." + nameItem + ".Type", "");
                if (!type.equalsIgnoreCase("")) {
                    if (type.contains(";")) {
                        String[] x = type.split(";");
                        if (Material.getMaterial(x[0]) == null) {
                            Main.messageGesturePaper.sendMessage("&c&lERROR WITH MATERIAL " + x[0] + " IN CONFIG.YML AT LINE: Interfaces." + nameInterface + ".Items." + nameItem + ".Type");
                            type = "DIRT";
                        }
                    } else {
                        if (Material.getMaterial(type) == null) {
                            Main.messageGesturePaper.sendMessage("&c&lERROR WITH MATERIAL " + type + " IN CONFIG.YML AT LINE: Interfaces." + nameInterface + ".Items." + nameItem + ".Type");
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
            Interface customInterface = new Interface(title, openSound, slots, itemsConfig, inputs, getDebug().get("ClickGui"),
                    contaSlots.size(), nameInterface, "", "");
            customInterface.initialize(getMessages().get("Success.Rename"), directRename, getMessages().get("Errors.InsufficientExperience"));
            interfaces.put(nameInterface, customInterface);
        }
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

    public SoundType getStageSound() {
        return stageSound;
    }

    public boolean isOnlyBedrock() {
        return onlyBedrock;
    }

    public PaymentConfig getFixHandPayment() {
        return fixHandPayment;
    }

    public PaymentConfig getFixInventoryPayment() {
        return fixInventoryPayment;
    }

    @Override
    public String toString() {
        return "ConfigGestion{" +
                "interfaces=" + interfaces +
                ", stageSound=" + stageSound +
                ", percentageDamage=" + percentageDamage +
                ", directRename=" + directRename +
                ", onlyBedrock=" + onlyBedrock +
                ", fixHandPayment=" + fixHandPayment +
                ", fixInventoryPayment=" + fixInventoryPayment +
                ", file=" + file +
                '}';
    }
}

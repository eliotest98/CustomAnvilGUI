package io.eliotesta98.CustomAnvilGUI.Interfaces;

import com.HeroxWar.HeroxCore.MessageGesture;
import com.HeroxWar.HeroxCore.Utils.Texture;
import com.HeroxWar.HeroxCore.Utils.TextureException;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemConfig {

    private String name, type, texture, soundClick, nameItemConfig;
    private List<String> lore;

    public ItemConfig(String nameItemConfig, String name, String type, String texture, List<String> lore, String soundClick) {
        this.name = name;
        this.type = type;
        this.texture = texture;
        this.lore = lore;
        this.soundClick = soundClick;
        this.nameItemConfig = nameItemConfig;
    }

    public ItemStack createItemConfig(String currentInterface, String nbt, int positionItem) {
        String[] nbtList = new String[]{};
        if (!nbt.equalsIgnoreCase("")) {
            nbtList = nbt.split(";");
        }
        ItemStack item;
        if (!type.contains(";"))// controllo la versione per settare l'item
            item = new ItemStack(Material.getMaterial(type));
        else {
            String[] x = type.split(";");
            item = new ItemStack(Material.getMaterial(x[0]), 1, Short.parseShort(x[1]));
        }
        if (type.equalsIgnoreCase("PLAYER_HEAD") || type.contains("SKULL")) {
            if (!texture.equalsIgnoreCase("")) {
                try {
                    Texture.setCustomTexture(item, texture);
                } catch (TextureException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        final ItemMeta itemm = item.getItemMeta();
        if (!lore.isEmpty()) {
            ArrayList<String> lorenew = new ArrayList<>();
            for (String lorePart : lore) {
                lorenew.add(MessageGesture.transformColor(lorePart)
                        .replace("{priceHand}", nbtList[0].split(":")[1])
                        .replace("{priceInventory}", nbtList[1].split(":")[1])
                );
            }
            itemm.setLore(lorenew);
        }
        String newName = name;
        if (name.contains("{exp}")) {
            String cost = "";
            for (String nbtString : nbtList) {
                String[] nbtSplit = nbtString.split(":");
                if (nbtSplit[0].equalsIgnoreCase("ap.experience")) {
                    cost = nbtSplit[1];
                    break;
                }
            }
            newName = name.replace("{exp}", cost);
        } else if (name.contains("{message}")) {
            String message = "";
            for (String nbtString : nbtList) {
                String[] nbtSplit = nbtString.split(":");
                if (nbtSplit[0].equalsIgnoreCase("ap.message")) {
                    message = nbtSplit[1];
                    break;
                }
            }
            newName = name.replace("{message}", message);
        }
        itemm.setDisplayName(MessageGesture.transformColor(newName));
        item.setItemMeta(itemm);
        if (item.getType().toString().equalsIgnoreCase("AIR")) {
            return item;
        }
        NBTItem nbtItem = new NBTItem(item);
        for (String nbtString : nbtList) {
            String[] nbtSplit = nbtString.split(":");
            try {
                int numberPage = Integer.parseInt(nbtSplit[1]);
                nbtItem.setInteger(nbtSplit[0], numberPage);
            } catch (Exception ex) {
                nbtItem.setString(nbtSplit[0], nbtSplit[1]);
            }
        }
        nbtItem.setInteger("ap.positionItem", positionItem);
        nbtItem.setString("ap.currentInterface", currentInterface);
        return nbtItem.getItem();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(ArrayList<String> lore) {
        this.lore = lore;
    }

    public String getSoundClick() {
        return soundClick;
    }

    public void setSoundClick(String soundClick) {
        this.soundClick = soundClick;
    }

    public String getNameItemConfig() {
        return nameItemConfig;
    }

    public void setNameItemConfig(String nameItemConfig) {
        this.nameItemConfig = nameItemConfig;
    }

    @Override
    public String toString() {
        return "ItemConfig{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", texture='" + texture + '\'' +
                ", soundClick='" + soundClick + '\'' +
                ", nameItemConfig='" + nameItemConfig + '\'' +
                ", lore=" + lore +
                '}';
    }
}

package io.eliotesta98.CustomAnvilGUI.Interfaces;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.eliotesta98.CustomAnvilGUI.Utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ItemConfig {

    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4");
    private String name, type, texture, soundClick, nameItemConfig;
    private ArrayList<String> lore;

    public ItemConfig(String nameItemConfig, String name, String type, String texture, ArrayList<String> lore, String soundClick) {
        this.name = name;
        this.type = type;
        this.texture = texture;
        this.lore = lore;
        this.soundClick = soundClick;
        this.nameItemConfig = nameItemConfig;
    }

    public ItemStack createItemConfig(String currentInterface, String nbt, int positionItem) {
        ItemStack item;
        if (!type.contains(";"))// controllo la versione per settare l'item
            item = new ItemStack(Material.getMaterial(type));
        else {
            String[] x = type.split(";");
            item = new ItemStack(Material.getMaterial(x[0]), 1, Short.parseShort(x[1]));
        }
        if (type.equalsIgnoreCase("PLAYER_HEAD") || type.contains("SKULL")) {
            if (!texture.equalsIgnoreCase("")) {
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                try {
                    PlayerProfile player_profile = Bukkit.createPlayerProfile(RANDOM_UUID);
                    PlayerTextures textures = player_profile.getTextures();
                    textures.setSkin(getUrlFromBase64(texture));
                    player_profile.setTextures(textures);
                    meta.setOwnerProfile(player_profile);
                } catch (NoSuchMethodError | MalformedURLException ignored) {
                    GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                    profile.getProperties().put("textures", new Property("textures", texture));
                    Field profileField;
                    try {
                        profileField = meta.getClass().getDeclaredField("profile");
                        profileField.setAccessible(true);
                        profileField.set(meta, profile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                item.setItemMeta(meta);
            }
        }
        final ItemMeta itemm = item.getItemMeta();
        if (!lore.isEmpty()) {
            ArrayList<String> lorenew = new ArrayList<>();
            for (String lorePart : lore) {
                lorenew.add(ColorUtils.applyColor(lorePart));
            }
            itemm.setLore(lorenew);
        }
        String newName = "";
        if (name.contains("{exp}")) {
            String cost = "";
            if (!nbt.equalsIgnoreCase("")) {
                String[] nbtList = nbt.split(";");
                for (String nbtString : nbtList) {
                    String[] nbtSplit = nbtString.split(":");
                    if (nbtSplit[0].equalsIgnoreCase("ap.experience")) {
                        cost = nbtSplit[1];
                        break;
                    }
                }
            }
            newName = name.replace("{exp}", cost);
        } else {
            newName = name;
        }
        itemm.setDisplayName(ColorUtils.applyColor(newName));
        item.setItemMeta(itemm);
        if (item.getType().toString().equalsIgnoreCase("AIR")) {
            return item;
        }
        NBTItem nbtItem = new NBTItem(item);
        if (!nbt.equalsIgnoreCase("")) {
            String[] nbtList = nbt.split(";");
            for (String nbtString : nbtList) {
                String[] nbtSplit = nbtString.split(":");
                try {
                    int numberPage = Integer.parseInt(nbtSplit[1]);
                    nbtItem.setInteger(nbtSplit[0], numberPage);
                } catch (Exception ex) {
                    nbtItem.setString(nbtSplit[0], nbtSplit[1]);
                }
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

    public ArrayList<String> getLore() {
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

    public static URL getUrlFromBase64(String base64) throws MalformedURLException {
        String decoded = new String(Base64.getDecoder().decode(base64));
        // We simply remove the "beginning" and "ending" part of the JSON, so we're left with only the URL. You could use a proper
        // JSON parser for this, but that's not worth it. The String will always start exactly with this stuff anyway
        return new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
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

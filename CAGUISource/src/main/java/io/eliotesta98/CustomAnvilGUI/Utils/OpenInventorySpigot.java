package io.eliotesta98.CustomAnvilGUI.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class OpenInventorySpigot {

    public static Inventory openInventory(Player player) {
        return openInventory(player, "");
    }

    public static Inventory openInventory(Player player, String title) {
        Block block = Blocks.ANVIL;
        ServerPlayer serverPlayer = getServerPlayer(player);
        Location loc = player.getLocation();
        ServerLevel serverLevel = ((CraftWorld) player.getWorld()).getHandle();
        serverPlayer.openMenu(block.defaultBlockState().getMenuProvider(serverLevel, new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
        serverPlayer.containerMenu.checkReachable = false;
        if (!title.equalsIgnoreCase("")) {
            serverPlayer.containerMenu.getBukkitView().setTitle(title);
        }
        return serverPlayer.containerMenu.getBukkitView().getTopInventory();
    }

    public static ServerPlayer getServerPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

}

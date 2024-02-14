package io.eliotesta98.CustomGuiForAnvil.Events;

import io.eliotesta98.CustomGuiForAnvil.Core.Main;
import io.eliotesta98.CustomGuiForAnvil.Interfaces.CustomGuiForAnvil;
import io.eliotesta98.CustomGuiForAnvil.Interfaces.Interface;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AnvilEvents implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnvilClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            Player player = event.getPlayer();
            if (clickedBlock.getType() == Material.ANVIL ||
                    clickedBlock.getType() == Material.CHIPPED_ANVIL ||
                    clickedBlock.getType() == Material.DAMAGED_ANVIL ||
                    clickedBlock.getType() == Material.LEGACY_ANVIL) {
                Main.instance.getConfigGestion().getInterfaces().get("Anvil").openInterface(player);
            }
        }
    }

}

package io.eliotesta98.CustomGuiForAnvil.Events;

import io.eliotesta98.CustomGuiForAnvil.Events.CustomEvents.CustomPrepareAnvilEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CustomPrepareAnvilListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCustomPrepareAnvilListener(CustomPrepareAnvilEvent event) {
        System.out.println(event.getResult());
        System.out.println(event.getAnvilInventory().getMaximumRepairCost());
    }

}

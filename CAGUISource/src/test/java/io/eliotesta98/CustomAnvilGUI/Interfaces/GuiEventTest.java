package io.eliotesta98.CustomAnvilGUI.Interfaces;

import io.eliotesta98.CustomAnvilGUI.Core.Main;
import org.bukkit.Location;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.InventoryView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

public class GuiEventTest {
    private static ServerMock serverMock;
    private static Main plugin;

    // Fake Instances
    private PlayerMock playerMock;
    private WorldMock worldMock;

    @BeforeEach
    public void setUp() {
        // Inizialization server and plugin
        serverMock = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);
        playerMock = serverMock.addPlayer();
        worldMock = playerMock.getWorld();
    }

    @AfterEach
    public void tearDown() {
        // Unmock Server and Plugin
        MockBukkit.unmock();
    }

    @Test
    public void testClickGui() {
        InventoryView anvilView = playerMock.openAnvil(new Location(worldMock, 0,0,0), false);
        serverMock.getPluginManager().callEvent(
                new InventoryClickEvent(anvilView, InventoryType.SlotType.CONTAINER, 1, ClickType.LEFT, InventoryAction.PICKUP_ALL));

        serverMock.getPluginManager().assertEventFired(InventoryClickEvent.class);
    }

}

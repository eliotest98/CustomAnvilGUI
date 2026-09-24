package io.eliotesta98.CustomAnvilGUI.Commands;

import io.eliotesta98.CustomAnvilGUI.Core.Main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

public class AnvilCommandTest {

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
    public void testPlayerWriteEvent() {
        plugin.getConfigGestion().setVirtualAnvilEnabled(true);
        plugin.onDisable();
        plugin.onEnable();
        serverMock.getScheduler().performTicks(100000);
        playerMock.chat("/anvil");

        //serverMock.getPluginManager().assertEventFired(PlayerCommandPreprocessEvent.class);
    }

}

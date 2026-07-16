package io.eliotesta98.CustomAnvilGUI.Events;

import io.eliotesta98.CustomAnvilGUI.Core.Main;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import java.util.HashSet;

// TODO to finish
public class PlayerWriteEventTest {

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
        serverMock.getPluginManager().callEvent(
                new AsyncPlayerChatEvent(false, playerMock,
                        "test", new HashSet<>(worldMock.getPlayers())));

        serverMock.getPluginManager().assertEventFired(AsyncPlayerChatEvent.class);
    }

}

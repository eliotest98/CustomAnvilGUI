package io.eliotesta98.CustomAnvilGUI.Core;

import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import java.util.Map;

public class MainTest {

    private static ServerMock serverMock;
    private static Main plugin;

    @BeforeEach
    public void setUp() {
        // Inizialization server and plugin
        serverMock = MockBukkit.mock();
        plugin = MockBukkit.load(Main.class);
    }

    @AfterEach
    public void tearDown() {
        // Unmock Server and Plugin
        MockBukkit.unmock();
    }

    @Test
    public void onEnable() {
        plugin.onEnable();
    }

    @Test
    public void onEnableWithDependencies() {
        Map<String, Boolean> hooks = plugin.getConfigGestion().getHooks();
        for (String hook : hooks.keySet()) {
            hooks.replace(hook, true);
            plugin.getConfigGestion().setHooks(hooks);
            MockBukkit.createMockPlugin(hook);
            plugin.onEnable();
            try {
                serverMock.getScheduler().performTicks(40);
            } catch (NullPointerException ignore) {

            }
            hooks.replace(hook, false);
            plugin.getConfigGestion().setHooks(hooks);

            plugin.onEnable();
            serverMock.getScheduler().performTicks(40);
        }
    }

}


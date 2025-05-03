package io.eliotesta98.CustomAnvilGUI.Module.Floodgate;

import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.UUID;

public class FloodgateUtils {

    private FloodgateApi floodgateApi;
    private boolean hooked;

    public FloodgateUtils() {
        hooked = false;
    }

    public void initialize() {
        floodgateApi = org.geysermc.floodgate.api.FloodgateApi.getInstance();
        hooked = true;
    }

    public boolean isBedrockPlayer(UUID uuid) {
        if (!hooked) {
            return false;
        }
        return floodgateApi.isFloodgatePlayer(uuid);
    }

    public FloodgatePlayer getBedrockPlayer(UUID uuid) {
        return floodgateApi.getPlayer(uuid);
    }

}

package io.eliotesta98.CustomAnvilGUI.Api;

import io.eliotesta98.CustomAnvilGUI.Core.Main;

/**
 * CustomAnvilGui API.
 *
 * @author eliotesta98
 * @version 1.0
 */
public class CustomAnvilGuiApi {

    private static final boolean isDebugEnabled = Main.instance.getConfigGestion().getDebug().get("API");

    /**
     * Return the sell price of a specific CubeGenerator with id and a multiplier.
     *
     * @param generatorId generator id.
     * @param multiplier  multiplier of price.
     * @return the sell price.
     */
    public static double price(int generatorId, double multiplier) {
        return 0.0;
    }

}

package com.calpayne.core.agent;

import com.calpayne.core.Settings;

/**
 *
 * @author Cal Payne
 */
public abstract class Agents {

    public static Agent createNew(Settings settings) {
        Agent agent;

        if (settings.getServerIP().equalsIgnoreCase("0.0.0.0")) {
            agent = new Server(settings);
        } else {
            agent = new Client(settings);
        }

        return agent;
    }

}

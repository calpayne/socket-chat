package com.calpayne;

import com.calpayne.core.Settings;
import com.calpayne.core.agent.Agent;
import com.calpayne.core.agent.Agents;

/**
 *
 * @author Cal Payne
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Settings settings = new Settings();
        settings.config();

        Agent agent = Agents.createNew(settings);
    }
}

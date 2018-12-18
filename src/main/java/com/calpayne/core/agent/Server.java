package com.calpayne.core.agent;

import com.calpayne.core.Settings;

/**
 *
 * @author Cal Payne
 */
public class Server extends Agent {

    public Server(Settings settings) {
        super(settings);
        super.startUp();
    }

    @Override
    protected void startUpSteps() {

    }
}

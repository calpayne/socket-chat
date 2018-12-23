package com.calpayne.core.agent;

import com.calpayne.core.Settings;
import com.calpayne.core.gui.ChatFrame;

/**
 *
 * @author Cal Payne
 */
public abstract class Agents {

    public static Agent createNew(Settings settings) {
        Agent agent;
        ChatFrame chatFrame = ChatFrame.getChatFrame();

        if (settings.getServerIP().equalsIgnoreCase("0.0.0.0")) {
            agent = new Server(settings, chatFrame);
        } else {
            agent = new Client(settings, chatFrame);
        }

        return agent;
    }

}

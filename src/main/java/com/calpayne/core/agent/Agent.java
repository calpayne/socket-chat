package com.calpayne.core.agent;

import com.calpayne.gui.ChatFrame;

/**
 *
 * @author Cal Payne
 */
public abstract class Agent {

    public Agent() {
        ChatFrame.getChatFrame().start();
    }

}

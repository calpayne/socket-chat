package com.calpayne;

import com.calpayne.core.Settings;
import com.calpayne.gui.ChatFrame;

/**
 *
 * @author Cal Payne
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Settings s = new Settings();
        s.config();
        ChatFrame.getChatFrame().start();
    }
}

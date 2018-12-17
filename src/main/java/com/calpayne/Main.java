package com.calpayne;

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
        ChatFrame app = new ChatFrame("Socket Chat");
        app.start();
    }
}

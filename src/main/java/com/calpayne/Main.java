package com.calpayne;

import com.calpayne.gui.ChatPanel;
import javax.swing.JFrame;

public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ChatPanel app = new ChatPanel("Socket Chat");
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setSize(470, 300);
        app.setLocationRelativeTo(null);
        app.setVisible(true);
        app.setAlwaysOnTop(true);
    }
}

package com.calpayne.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Cal Payne
 */
public class ChatPanel extends JFrame {

    public ChatPanel(String title) {
        super(title);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        JPanel container2 = new JPanel();
        container2.setBackground(Color.WHITE);
        container2.setLayout(new GridBagLayout());

        JTextField input = new JTextField(30);
        input.setBorder(BorderFactory.createCompoundBorder(input.getBorder(), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JButton sendBtn = new JButton("Send Message");
        sendBtn.addActionListener((ActionEvent ae) -> {
            System.out.println("Send: " + input.getText());
            input.setText("");
        });

        JTextArea messages = new JTextArea("Welcome to the chat!");
        messages.setEditable(false);
        messages.setLineWrap(true);
        messages.setBorder(BorderFactory.createCompoundBorder(messages.getBorder(), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        container2.add(input, left);
        container2.add(sendBtn, right);

        container.add(new JScrollPane(messages), BorderLayout.CENTER);
        container.add(BorderLayout.SOUTH, container2);

        this.add(container);
    }

}

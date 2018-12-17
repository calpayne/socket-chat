package com.calpayne.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 *
 * @author Cal Payne
 */
public class ChatFrame extends JFrame {

    public ChatFrame(String title) {
        super(title);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        JPanel container2 = new JPanel();
        container2.setLayout(new GridBagLayout());

        JTextField input = new JTextField(30);
        input.setBorder(BorderFactory.createCompoundBorder(input.getBorder(), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JButton sendBtn = new JButton("Send Msg");
        sendBtn.addActionListener((ActionEvent ae) -> {
            System.out.println("Send: " + input.getText());
            input.setText("");
        });

        JTextPane messages = new JTextPane();
        messages.setContentType("text/html");
        messages.setText("<html><style type=\"text/css\">\n"
                + ".nm1 {padding: 10px;margin-top: 5px;color: #0c5460;background-color: #d1ecf1;border: 1px solid #bee5eb;}\n"
                + ".nm2 {padding: 10px;margin-top: 5px;color: #004085; background-color: #cce5ff; border: 1px solid #b8daff;}\n"
                + ".sm {padding: 10px;margin-top: 5px;color: #856404;background-color: #fff3cd;border: 1px solid #ffeeba;}\n"
                + "</style>\n"
                + "<div class=\"sm\"><b>Server:</b> Welcome to the chat! Type <b>/help</b> for help</div>\n"
                + "<div class=\"nm1\"><b>Callum:</b> Hello everyone</div>\n"
                + "<div class=\"nm2\"><b>Callum2:</b> Hello Callum</div>\n"
                + "</html>");
        messages.setEditable(false);
        messages.setBorder(BorderFactory.createCompoundBorder(messages.getBorder(), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel clients = new JLabel("<html><style type=\"text/css\">p {font-weight: 300;}</style><b>Who's Online</b><br /><br /><p>Calp</p></html>");
        clients.setVerticalAlignment(JLabel.TOP);
        clients.setVerticalTextPosition(JLabel.TOP);
        clients.setBorder(BorderFactory.createCompoundBorder(messages.getBorder(), BorderFactory.createEmptyBorder(4, 4, 4, 4)));

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
        container.add(container2, BorderLayout.SOUTH);
        container.add(new JScrollPane(clients), BorderLayout.EAST);

        this.add(container);
    }

    public void start() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
    }

}

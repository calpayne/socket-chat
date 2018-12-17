package com.calpayne.gui;

import com.calpayne.message.Message;
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

    private static ChatFrame CHAT_FRAME;
    private final JTextPane messages;

    private ChatFrame(String title) {
        super(title);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        JPanel container2 = new JPanel();
        container2.setLayout(new GridBagLayout());

        JTextField input = new JTextField(30);
        input.setBorder(BorderFactory.createCompoundBorder(input.getBorder(), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        messages = new JTextPane();
        messages.setContentType("text/html");
        messages.setText("<html><style type=\"text/css\">\n"
                + ".nm {padding: 10px;margin-top: 5px;color: #004085; background-color: #cce5ff; border: 1px solid #b8daff;}\n"
                + ".sm {padding: 10px;margin-top: 5px;color: #856404;background-color: #fff3cd;border: 1px solid #ffeeba;}\n"
                + "</style>\n"
                + "<div class=\"sm\"><b>Server:</b> Welcome to the chat! Type <b>/help</b> for help</div>\n"
                + "</html>");
        messages.setEditable(false);
        messages.setBorder(BorderFactory.createCompoundBorder(messages.getBorder(), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel clients = new JLabel("<html><style type=\"text/css\">p {margin-top: 2px;font-weight: 300;} p b {color: #856404;}</style><b>Who's Online</b><br /><br /><p><img src=\"file:star.png\"><b>Callum</b></p><p>123</p></html>");
        clients.setVerticalAlignment(JLabel.TOP);
        clients.setVerticalTextPosition(JLabel.TOP);
        clients.setBorder(BorderFactory.createCompoundBorder(messages.getBorder(), BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        JButton sendBtn = new JButton("Send Msg");
        sendBtn.addActionListener((ActionEvent ae) -> {
            Message message = new Message("Callum", input.getText());
            addMessageToView(message);
            input.setText("");
        });

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

    public static ChatFrame getChatFrame() {
        if (CHAT_FRAME == null) {
            CHAT_FRAME = new ChatFrame("Socket Chat");
        }

        return CHAT_FRAME;
    }

    public void start() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
    }

    public void addMessageToView(Message message) {
        String current = messages.getText();
        current = current.substring(0, current.indexOf("</body>"));
        current += message.toString() + "</body></html>";

        messages.setText(current);

    }

}

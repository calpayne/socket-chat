package com.calpayne.core.gui;

import com.calpayne.core.agent.Agent;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.MessageType;
import com.calpayne.core.message.types.OnlineListDataMessage;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Cal Payne
 */
public class ChatFrame extends JFrame {

    private static ChatFrame CHAT_FRAME;
    private Agent agent;
    private final OnlineList onlineList;
    private final JTextPane messages;

    /**
     * @param title the title to use
     */
    private ChatFrame(String title) {
        super(title);
        onlineList = new OnlineList();

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
                + ".em {padding: 10px;margin-top: 5px;color: #721c24;background-color: #f8d7da;border: 1px solid #f5c6cb;}\n"
                + "</style>\n"
                + "<div class=\"sm\"><b>Server:</b> Welcome to the chat! Type <b>/help</b> for help</div>\n"
                + "</html>");
        messages.setEditable(false);
        messages.setBorder(BorderFactory.createCompoundBorder(messages.getBorder(), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        DefaultCaret caret = (DefaultCaret) messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JButton sendBtn = new JButton("Send Msg");
        sendBtn.addActionListener((ActionEvent ae) -> {
            if (agent != null && !input.getText().isEmpty() && input.getText().matches("^[a-zA-Z0-9,.!?:/ ]*$")) {
                Message message = new Message(agent.getHandle(), input.getText());
                agent.sendMessage(message);
                input.setText("");
            } else {
                addMessageToView(new Message(MessageType.ERROR, "Server", "Your message could not be sent."));
            }
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
        container.add(new JScrollPane(onlineList), BorderLayout.EAST);

        this.add(container);
    }

    /**
     * @param agent the agent to set
     */
    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    /**
     * @return the ChatFrame instance
     */
    public static ChatFrame getChatFrame() {
        if (CHAT_FRAME == null) {
            CHAT_FRAME = new ChatFrame("Socket Chat");
        }

        return CHAT_FRAME;
    }

    /**
     * Show the frame
     */
    public void start() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
    }

    /**
     * @param message add a message to the view
     */
    public synchronized void addMessageToView(Message message) {
        SwingUtilities.invokeLater(() -> {
            String current = messages.getText();
            current = current.substring(0, current.indexOf("</body>"));
            current += message.toString() + "</body></html>";
            messages.setText(current);
        });
    }

    public ArrayList<String> getOnlineList() {
        return onlineList.getOnlineList();
    }

    public void addClient(String handle) {
        onlineList.addClient(handle);
    }

    public void updateOnlineList(OnlineListDataMessage oldm) {
        onlineList.updateList(oldm);
    }

    public void removeClient(String handle) {
        onlineList.removeClient(handle);
    }

}

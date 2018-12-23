package com.calpayne.core.gui;

import com.calpayne.core.Nametag;
import com.calpayne.core.Rank;
import com.calpayne.core.agent.Agent;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.MessageType;
import com.calpayne.core.message.types.OnlineListDataMessage;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author Cal Payne
 */
public class ChatFrame extends JFrame {

    private static ChatFrame CHAT_FRAME;
    private Agent agent;
    private final OnlineList onlineList;
    private final JScrollPane messagesScrollPane;
    private final JTextPane messages;
    private final JTextField input;
    private String lastSentText;

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

        input = new JTextField(30);
        input.setBorder(BorderFactory.createCompoundBorder(input.getBorder(), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendAMessage();
                }
            }

        });

        messages = new JTextPane();
        messages.setContentType("text/html");
        messages.setText("<html><style type=\"text/css\">\n"
                + ".nm {padding: 10px;margin-top: 5px;color: #004085; background-color: #cce5ff; border: 1px solid #b8daff;}\n"
                + ".sm {padding: 10px;margin-top: 5px;color: #856404;background-color: #fff3cd;border: 1px solid #ffeeba;}\n"
                + ".wm {padding: 10px;margin-top: 5px;color: #155724;background-color: #d4edda;border: 1px solid #c3e6cb;}\n"
                + ".em {padding: 10px;margin-top: 5px;color: #721c24;background-color: #f8d7da;border: 1px solid #f5c6cb;}\n"
                + "</style>\n"
                + "</html>");
        messages.setEditable(false);
        messages.setBorder(BorderFactory.createCompoundBorder(messages.getBorder(), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        DefaultCaret caret = (DefaultCaret) messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        messagesScrollPane = new JScrollPane(messages);

        JButton sendBtn = new JButton("Send Msg");
        sendBtn.addActionListener((ActionEvent ae) -> {
            sendAMessage();
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

        container.add(messagesScrollPane, BorderLayout.CENTER);
        container.add(container2, BorderLayout.SOUTH);
        container.add(new JScrollPane(onlineList), BorderLayout.EAST);

        this.add(container);
    }
    
    public void setGuiAlwaysOnTop(boolean guiAlwaysOnTop) {
        this.setAlwaysOnTop(guiAlwaysOnTop);
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

    private void sendAMessage() {
        if (agent != null && !input.getText().isEmpty() && !input.getText().trim().isEmpty() && input.getText().matches("^[a-zA-Z0-9,.!?:/ ]*$")) {
            if (input.getText().length() > 100) {
                addMessageToView(new Message(MessageType.ERROR, "Server", "Your message could not be sent as it is longer than 100 characters."));
            } else {
                if (lastSentText != null && lastSentText.trim().equalsIgnoreCase(input.getText().trim())) {
                    addMessageToView(new Message(MessageType.ERROR, "Server", "You have already sent this message."));
                } else {
                    Message message = new Message(agent.getHandle(), input.getText());
                    agent.sendMessage(message);
                }
                lastSentText = input.getText();
                input.setText("");
            }
        } else {
            addMessageToView(new Message(MessageType.ERROR, "Server", "Your message could not be sent as it has illegal characters or is empty."));
        }
    }

    /**
     * Show the frame
     */
    public void start() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * @param message add a message to the view
     */
    public synchronized void addMessageToView(Message message) {
        SwingUtilities.invokeLater(() -> {
            try {
                HTMLDocument doc = (HTMLDocument) messages.getStyledDocument();
                HTMLEditorKit kit = (HTMLEditorKit) messages.getEditorKit();

                kit.insertHTML(doc, doc.getLength(), message.toString(), 0, 0, null);

                messages.validate();
                messagesScrollPane.validate();

                JScrollBar sb = messagesScrollPane.getVerticalScrollBar();
                sb.setValue(sb.getMaximum());
            } catch (IOException | BadLocationException ex) {

            }
        });
    }

    public void setRank(String handle, Rank rank) {
        onlineList.setRank(handle, rank);
    }

    public ArrayList<Nametag> getOnlineList() {
        return onlineList.getOnlineList();
    }

    public void addClient(Nametag nametag) {
        onlineList.addClient(nametag);
    }

    public void updateOnlineList(OnlineListDataMessage oldm) {
        onlineList.updateList(oldm);
    }

    public void removeClient(String handle) {
        onlineList.removeClient(handle);
    }

}

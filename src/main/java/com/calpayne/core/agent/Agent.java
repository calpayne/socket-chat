package com.calpayne.core.agent;

import com.calpayne.core.Settings;
import com.calpayne.core.gui.ChatFrame;
import com.calpayne.core.message.Message;
import com.calpayne.core.message.handler.MessageHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author Cal Payne
 */
public abstract class Agent {

    protected final ChatFrame chatFrame;
    protected final Settings settings;
    private MessageHandler handler;
    private final BlockingQueue<Message> messages = new LinkedBlockingDeque<>();

    private final Thread handleMessages = new Thread(() -> {
        while (true) {
            try {
                Message newMessage = messages.take();
                handler.handleMessage(this, newMessage);
            } catch (InterruptedException ex) {

            }
        }
    });

    /**
     * @param settings the settings to use
     * @param handler the handler to use
     */
    public Agent(Settings settings, MessageHandler handler) {
        this.settings = settings;
        this.handler = handler;
        chatFrame = ChatFrame.getChatFrame();
        chatFrame.setAgent(this);
    }

    /**
     * Startup the Agent
     */
    public final void startup() {
        startupSteps();
        startupThreads();
        handleMessages.start();
        chatFrame.start();
    }

    /**
     * Additional startup steps
     */
    protected abstract void startupSteps();

    /**
     * Startup threads
     */
    protected abstract void startupThreads();

    /**
     * @param handler the handler to set
     */
    public void setMessageHandler(MessageHandler handler) {
        this.handler = handler;
    }

    /**
     * @param message the message to queue
     * @throws java.lang.InterruptedException
     */
    protected void queueMessage(Message message) throws InterruptedException {
        messages.put(message);
    }

    /**
     * @param message the message to send
     */
    public abstract void sendMessage(Message message);

    /**
     * @return the handle
     */
    public String getHandle() {
        return settings.getHandle();
    }

    /**
     * @param message the message to add to the view
     */
    public synchronized void addMessageToView(Message message) {
        chatFrame.addMessageToView(message);
    }

}

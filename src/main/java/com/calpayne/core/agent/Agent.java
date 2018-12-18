package com.calpayne.core.agent;

import com.calpayne.core.Settings;
import com.calpayne.gui.ChatFrame;
import com.calpayne.message.Message;
import com.calpayne.message.handler.MessageHandler;
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
    protected final Object queueLock = new Object();
    private final BlockingQueue<Message> messages = new LinkedBlockingDeque<>();

    private final Thread handleMessages = new Thread(() -> {
        while (true) {
            try {
                if (handler != null) {
                    synchronized (queueLock) {
                        Message newMessage = messages.take();
                        handler.handleMessage(this, newMessage);
                    }
                }
            } catch (InterruptedException ex) {

            }
        }
    });

    public Agent(Settings settings) {
        this.settings = settings;
        chatFrame = ChatFrame.getChatFrame();
    }

    public final void startUp() {
        if (startUpSteps()) {
            startUpThreads();
            handleMessages.start();
            chatFrame.start();
        }
    }

    protected abstract boolean startUpSteps();

    protected abstract void startUpThreads();

    public void setMessageHandler(MessageHandler handler) {
        this.handler = handler;
    }

    protected void queueMessage(Message message) throws InterruptedException {
        synchronized (queueLock) {
            messages.put(message);
        }
    }
    
    public abstract void sendMessage(Message message);

    public String getHandle() {
        return settings.getHandle();
    }
    
}

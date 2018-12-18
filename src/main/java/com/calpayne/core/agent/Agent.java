package com.calpayne.core.agent;

import com.calpayne.core.Settings;
import com.calpayne.gui.ChatFrame;
import com.calpayne.message.Message;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author Cal Payne
 */
public abstract class Agent {

    protected final ChatFrame chatFrame;
    protected final Settings settings;
    protected final Object queueLock = new Object();
    private final BlockingQueue<Message> messages = new LinkedBlockingDeque<>();

    public Agent(Settings settings) {
        this.settings = settings;
        chatFrame = ChatFrame.getChatFrame();
    }
    
    public final void startUp() {
        startUpSteps();
        chatFrame.start();
    }
    
    protected abstract void startUpSteps();

}

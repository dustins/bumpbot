/*
 * Copyright
 */

package net.notgandhi.turntable.listeners;

import org.l3eta.tt.Bot;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.event.UserJoinEvent;
import org.l3eta.tt.event.UserLeaveEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class Description
 *
 * @author Dustin Sweigart <dustin@swigg.net>
 */
public class GreetingListener extends EventListener {

    static private final Logger LOGGER = LoggerFactory.getLogger(GreetingListener.class);

    private String ignoreID;

    public GreetingListener() {
    }

    public GreetingListener(String ignoreID) {
        this.ignoreID = ignoreID;
    }

    @EventMethod
    public void onLeave(UserLeaveEvent event) {
        LOGGER.info("{} has left the room.", event.getUser().getName());
    }

    @EventMethod
    public void onJoin(UserJoinEvent event) {
        if (event.getUser().getID().equals(this.ignoreID)) {
            return;
        }

        final String userName = event.getUser().getName();
        final Bot bot = this.bot;
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                bot.speak(String.format("Welcome to the room %s!", userName));
            }
        }, 2, TimeUnit.SECONDS);

        LOGGER.info("{} has joined the room!", event.getUser().getName());
    }
}

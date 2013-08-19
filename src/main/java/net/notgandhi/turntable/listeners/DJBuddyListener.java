/*
 * Copyright
 */

package net.notgandhi.turntable.listeners;

import org.l3eta.tt.Bot;
import org.l3eta.tt.event.*;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.util.BotMessage;
import org.l3eta.tt.util.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class Description
 *
 * @author Dustin Sweigart <dustin@swigg.net>
 */
public class DJBuddyListener extends EventListener {

    static private final Logger LOGGER = LoggerFactory.getLogger(DJBuddyListener.class);

    private String ignoreID;

    private Boolean shouldStepDown = false;

    public DJBuddyListener() {
        super();
    }

    public DJBuddyListener(String ignoreID) {
        this();
        this.ignoreID = ignoreID;
    }

    @EventMethod
    public void onLeave(UserLeaveEvent event) {
        LOGGER.info("{} has left the room.", event.getUser().getName());
    }

    @EventMethod
    public void onJoin(UserJoinEvent event) {
        final String userName = event.getUser().getName();
        final Bot bot = this.bot;
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                if (bot.getRoom().getDjs().length == 0) {
                    bot.speak(String.format("If somebody wants to DJ, I'll step up with them."));
                }
            }
        }, 2, TimeUnit.SECONDS);

        if (this.bot.getSelf().getID().equals(event.getUser().getID())) {
            this.dj();
        }

        LOGGER.info("{} has joined the room!", event.getUser().getName());
    }

    @EventMethod
    public void onSongChange(SongEvent songEvent) {
        if (this.shouldStepDown) {
            this.shouldStepDown = false;

            final Bot bot = this.bot;
            bot.speak(String.format("Well I'm not going to DJ by myself!"), new BotMessage.MessageCallback() {
                @Override
                public void run(Message message) {
                    bot.remDj();
                }
            });
        }
    }

    @EventMethod
    public void onDjChange(DjEvent event) {
        if (event.isDjing()) {
            this.onDjJoin(event);
        } else {
            this.onDjLeave(event);
        }
    }

    public void onDjJoin(DjEvent event) {
        if (this.bot.getRoom().getDjs().length == 1) {
            this.dj();
        }
        else if (this.bot.getRoom().getDjs().length > 2) {
            this.shouldStepDown = true;
        }
    }

    private void dj() {
        final Bot bot = this.bot;
        this.bot.dj(new BotMessage.MessageCallback() {
            @Override
            public void run(Message message) {
                bot.speak("I'll join you until more humans show up!");
            }
        });
    }

    public void onDjLeave(DjEvent event) {
        if (this.bot.getRoom().getDjs().length == 1) {
            this.shouldStepDown = true;
        }
    }
}

/*
 * Copyright
 */

package com.notgandhi.turntable.listeners;

import com.google.common.base.Optional;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.l3eta.tt.Bot;
import org.l3eta.tt.Song;
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
        LOGGER.info("{} has joined the room!", event.getUser().getName());

        if (this.bot.getSelf().getID().equals(event.getUser().getID())) {
            final DJBuddyListener buddyListener = this;
            Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    buddyListener.onDjJoin();
                }
            }, 2, TimeUnit.SECONDS);
        }

        final Bot bot = this.bot;
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                if (bot.getRoom().getDjs().length == 0) {
                    bot.speak(String.format("If somebody wants to DJ, I'll step up with them."));
                }
            }
        }, 2, TimeUnit.SECONDS);

    }

    @EventMethod
    public void onSongChange(EndSongEvent songEvent) {
//        if (this.bot.getRoom().getDjs().length <= 1) {
//            if (this.isCurrentlyPlaying()) {
//                leave the table before the song is over
//                this.leaveTheTable("Well I'm not going to DJ by myself!");
//            }
//        }

        if (this.bot.getRoom().getDjs().length > 2) {
            if (!this.isCurrentlyPlaying()) {
                this.leaveTheTable("Seems like you humans want to DJ for a while!");
            }
        }
    }

    @EventMethod
    public void onDjChange(DjEvent event) {
        if (event.isDjing()) {
            this.onDjJoin();
        } else {
            this.onDjLeave();
        }
    }

    public void joinTheTable() {
        final Bot bot = this.bot;
        this.bot.dj(new BotMessage.MessageCallback() {
            @Override
            public void run(Message message) {
                bot.speak("I'll join you until more humans show up!");
            }
        });
    }

    public void leaveTheTable(String message) {
        if (this.isCurrentlyPlaying()) {
            Song song = this.bot.getRoom().getCurrentSong();
            Optional<LocalDateTime> startTime = song.getStartTime();
            LocalDateTime endTime = startTime.get().plus(song.getDuration()).minusSeconds(3);
            Duration endDuration = new Duration(LocalDateTime.now().toDateTime(), endTime.minusSeconds(3).toDateTime());
            Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    bot.remDj();
                }
            }, endDuration.getStandardSeconds(), TimeUnit.SECONDS);
            LOGGER.info("Steping down as DJ in {} seconds", endDuration.getStandardSeconds());
        }
        else {
            LOGGER.info("Steping down as DJ");
            final Bot bot = this.bot;
            bot.speak(String.format(message), new BotMessage.MessageCallback() {
                @Override
                public void run(Message message) {
                    bot.remDj();
                }
            });
        }
    }

    public void onDjJoin() {
        if (this.bot.getRoom().getDjs().length == 1) {
            this.joinTheTable();
        }
        else if (this.bot.getRoom().getDjs().length > 2) {
            this.leaveTheTable("Seems like you humans want to DJ for a while!");
        }
    }

    public void onDjLeave() {
        if (this.bot.getRoom().getDjs().length == 1) {
            this.leaveTheTable("Well I'm not going to DJ by myself!");
        }
    }

    public boolean isCurrentlyPlaying() {
        return this.bot.getRoom().getCurrentSong().getUser().getID().equals(this.bot.getSelf().getID());
    }
}

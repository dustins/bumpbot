/*
 * Copyright
 */

package com.notgandhi.turntable.commands;

import org.l3eta.tt.User;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.event.Event.EventMethod;
import org.l3eta.tt.event.EventListener;
import org.l3eta.tt.event.SongEvent;
import org.l3eta.tt.user.Rank;

/**
 * Class Description
 *
 * @author Dustin Sweigart <dustin@swigg.net>
 */
public class SkipCommand extends Command {
    public static final String NAME = "skip";

    public boolean isCurrentDj = false;

    public SkipCommand(String name) {
        super(name);
        this.setRank(Rank.OWNER, Rank.MOD);
    }

    public SkipCommand() {
        this(NAME);
    }

    @Override
    public void load() {
        this.bot.getEventManager().addListener(new SongChangeListener());
    }

    @Override
    public void execute(User user, String[] args, ChatEvent.ChatType type) {
        if (this.isCurrentDj) {
            this.bot.skipSong();
            this.bot.speak("Fine! I'll skip the stupid song.");
            this.isCurrentDj = false;
        } else {
            this.bot.speak("Can't skip, I'm not playing the current song.");
        }
    }

    public class SongChangeListener extends EventListener {
        @EventMethod
        public void songChange(SongEvent event) {
            isCurrentDj = this.bot.getSelf().getID().equals(event.getUser().getID());
        }
    }
}

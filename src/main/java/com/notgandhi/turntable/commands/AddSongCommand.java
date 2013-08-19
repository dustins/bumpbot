/*
 * Copyright
 */

package com.notgandhi.turntable.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.l3eta.tt.Song;
import org.l3eta.tt.User;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.user.Rank;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.emptyToNull;

/**
 * Class Description
 *
 * @author Dustin Sweigart <dustin@swigg.net>
 */
public class AddSongCommand extends Command {
    public static final String NAME = "add-song";

    public AddSongCommand(String name) {
        super(name);
        this.setRank(Rank.OWNER, Rank.MOD);
    }

    public AddSongCommand() {
        this(NAME);
    }

    @Override
    public void execute(User user, String[] args, ChatEvent.ChatType type) {
        List<Song> songs = bot.getRoom().getSongLog().getList();

        if (songs.size() < 1) {
            bot.speak("Sorry, there are no songs to add.");
            return;
        }

        if (bot.getRoom().getCurrentSong() == null && args.length == 0) {
            bot.speak("Sorry, there is no playing song.");
            return;
        }

        Song song = null;
        try {
            song = this.getLastSong(songs, args);
        } catch (IllegalArgumentException exception) {
            bot.speak(exception.getMessage());
            return;
        }

        bot.addSong(song.getID());

        List<String> parts = Lists.newArrayList();
        List<String> variables = Lists.newArrayList();

        parts.add("Added `%s`");
        variables.add(song.getName());

        if (emptyToNull(song.getArtist()) != null) {
            parts.add("by `%s`");
            variables.add(song.getArtist());
        }

        String message = Joiner.on(" ").join(parts).concat(" to my playlist!");
        bot.speak(String.format(message, variables.toArray(new String[]{})));
    }

    private Song getLastSong(List<Song> songs, String[] args) {
        if (args.length > 0) {
            try {
                Integer songNumber = 0;
                songNumber = Integer.parseInt(args[0]);
                checkArgument(songNumber > 0, "Song number must be greater or equal to 1");
                checkArgument(songNumber <= songs.size(), "There are only %s recently played songs", songs.size());

                return bot.getRoom().getSongLog().getList().get(songNumber - 1);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(String.format("Unable to parse %s", args[0]), exception);
            }
        }

        return bot.getRoom().getSongLog().getList().get(0);
    }
}

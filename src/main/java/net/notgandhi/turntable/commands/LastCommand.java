/*
 * Copyright
 */

package net.notgandhi.turntable.commands;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.l3eta.tt.Song;
import org.l3eta.tt.User;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.event.ChatEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.emptyToNull;

/**
 * Class Description
 *
 * @author Dustin Sweigart <dustin@swigg.net>
 */
public class LastCommand extends Command {
    public static final String NAME = "last";

    public LastCommand() {
        super(NAME);
    }

    @Override
    public void execute(User user, String[] args, ChatEvent.ChatType type) {
        List<Song> songs = bot.getRoom().getSongLog().getList();

        if (songs.size() < 2) {
            bot.speak("Sorry, there were no previous songs.");
        }

        int lastIndex = 0;
        try {
            lastIndex = this.getLastIndex(songs, args);
        } catch (IllegalArgumentException exception) {
            bot.speak(exception.getMessage());
            return;
        }

        Song song = songs.get(lastIndex);
        List<String> parts = Lists.newArrayList();
        List<String> variables = Lists.newArrayList();

        parts.add("Last song played was`%s`");
        variables.add(song.getName());

        if (emptyToNull(song.getArtist()) != null) {
            parts.add("by `%s`");
            variables.add(song.getArtist());
        }

        if (emptyToNull(song.getAlbum()) != null) {
            parts.add("from the album `%s`");
            variables.add(song.getAlbum());
        }

        String message = Joiner.on(" ").join(parts);
        bot.speak(String.format(message, variables.toArray(new String[]{})));
    }

    private int getLastIndex(List<Song> songs, String[] args) {
        if (args.length > 0) {
            Integer songNumber = 0;
            try {
                songNumber = Integer.parseInt(args[0]);
                checkArgument(songNumber > 0, "Last number must be greater or equal to 1");
                checkArgument(songNumber <= songs.size(), "There are only %s recently played songs", songs.size());

                return (songNumber - 1);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(String.format("Unable to parse %s", args[0]), exception);
            }
        }

        return bot.getRoom().getCurrentSong() == null ? 0 : 1;
    }
}

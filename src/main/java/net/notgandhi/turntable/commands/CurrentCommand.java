/*
 * Copyright
 */

package net.notgandhi.turntable.commands;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.l3eta.tt.Song;
import org.l3eta.tt.User;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.event.ChatEvent;
import org.l3eta.tt.user.Rank;

import java.util.List;

import static com.google.common.base.Strings.emptyToNull;

/**
 * Class Description
 *
 * @author Dustin Sweigart <dustin@swigg.net>
 */
public class CurrentCommand extends Command {
    public static final String NAME = "current";

    public CurrentCommand(String name) {
        super(name);
        this.setRank(Rank.OWNER, Rank.MOD, Rank.USER);
    }

    public CurrentCommand() {
        this(NAME);
    }

    @Override
    public void execute(User user, String[] args, ChatEvent.ChatType type) {
        Optional<Song> song = Optional.fromNullable(bot.getRoom().getCurrentSong());

        if (!song.isPresent()) {
            bot.speak("There's no song currently playing. Try `/last` to get the last song's info.");
            return;
        }

        List<String> parts = Lists.newArrayList();
        List<String> variables = Lists.newArrayList();

        parts.add("Currently playing `%s`");
        variables.add(song.get().getName());

        if (emptyToNull(song.get().getArtist()) != null) {
            parts.add("by `%s`");
            variables.add(song.get().getArtist());
        }

        if (emptyToNull(song.get().getAlbum()) != null) {
            parts.add("from the album `%s`");
            variables.add(song.get().getAlbum());
        }

        String message = Joiner.on(" ").join(parts);
        bot.speak(String.format(message, variables.toArray(new String[]{})));
    }
}

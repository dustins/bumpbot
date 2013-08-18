/*
 * Copyright
 */

package net.notgandhi.turntable.config;

import net.notgandhi.turntable.BumpBot;
import net.notgandhi.turntable.commands.AddSongCommand;
import net.notgandhi.turntable.commands.CurrentCommand;
import net.notgandhi.turntable.commands.LastCommand;
import net.notgandhi.turntable.listeners.GreetingListener;
import org.l3eta.tt.command.Command;
import org.l3eta.tt.event.EventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Class Description
 *
 * @author Dustin Sweigart <dustin@swigg.net>
 */
@Configuration
@PropertySource("classpath:auth-credentials.properties")
public class AppConfig {
    @Bean
    public BumpBot bot(List<EventListener> listeners, List<Command> commands, Environment environment) {
        String auth = environment.getRequiredProperty("auth");
        String userid = environment.getRequiredProperty("userid");
        String roomid = environment.getRequiredProperty("roomid");
        BumpBot bot = new BumpBot(auth, userid, roomid);

        bot.getEventManager().addListeners(listeners.toArray(new EventListener[]{}));
        bot.getCommandManager().addCommands(commands.toArray(new Command[]{}));

        return bot;
    }

    @Bean
    public GreetingListener greetingListener(Environment environment) {
        String ignoreID = environment.getProperty("userid");
        return new GreetingListener(ignoreID);
    }

    @Bean
    public CurrentCommand currentCommand() {
        return new CurrentCommand();
    }

    @Bean
    public LastCommand lastCommand() {
        return new LastCommand();
    }

    @Bean
    public AddSongCommand addSongCommand() {
        return new AddSongCommand();
    }
}

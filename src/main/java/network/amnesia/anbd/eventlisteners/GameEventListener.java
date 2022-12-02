package network.amnesia.anbd.eventlisteners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import network.amnesia.anbd.games.HangmanGame;
import network.amnesia.anbd.games.OxoGame;
import org.jetbrains.annotations.NotNull;

public class GameEventListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            OxoGame.getGames().stream().filter(oxoGame -> oxoGame.getThreadChannel() == event.getChannel().getIdLong()).findFirst().ifPresent(oxoGame -> oxoGame.onMessageReceived(event)); //Oxo Listener
            HangmanGame.getGames().stream().filter(hangmanGame -> hangmanGame.getThreadChannel() == event.getChannel().getIdLong()).findFirst().ifPresent(hangmanGame -> hangmanGame.onMessageReceived(event)); //Hangman Listener
        }
    }
}

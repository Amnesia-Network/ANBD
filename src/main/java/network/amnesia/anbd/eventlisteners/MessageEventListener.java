package network.amnesia.anbd.eventlisteners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import network.amnesia.anbd.games.HangmanGame;
import network.amnesia.anbd.games.OxoGame;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MessageEventListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            //GAMES
            OxoGame.getGames().stream().filter(oxoGame -> oxoGame.getThreadChannel() == event.getChannel().getIdLong()).findFirst().ifPresent(oxoGame -> oxoGame.onMessageReceived(event)); //Oxo Listener
            HangmanGame.getGames().stream().filter(hangmanGame -> hangmanGame.getThreadChannel() == event.getChannel().getIdLong()).findFirst().ifPresent(hangmanGame -> hangmanGame.onMessageReceived(event)); //Hangman Listener

            //LATEX
            if (event.getMessage().getContentRaw().trim().length() > 2 && event.getMessage().getContentRaw().trim().charAt(0) == '$' && event.getMessage().getContentRaw().trim().charAt(event.getMessage().getContentRaw().length() - 1) == '$') {
                Message latexMessage = event.getMessage();
                Message messageReply = latexMessage.reply(getLatexUrl(latexMessage.getContentRaw())).setSuppressedNotifications(true).complete();
                messageReply.addReaction(Emoji.fromUnicode("🚮")).queue();
            }
        }
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        //LATEX
        if (event.getMessage().getContentRaw().trim().length() > 2 && event.getMessage().getContentRaw().trim().charAt(0) == '$' && event.getMessage().getContentRaw().trim().charAt(event.getMessage().getContentRaw().length() - 1) == '$') {
            Message latexUpdatedMessage = event.getMessage();
            String latexUrl = getLatexUrl(latexUpdatedMessage.getContentRaw());
            for (Message message : event.getChannel().getHistory().retrievePast(40).complete()) {
                if (message.getReferencedMessage() != null && latexUpdatedMessage.getIdLong() == message.getReferencedMessage().getIdLong() && message.getAuthor().isBot() && message.getContentRaw().contains("https://latex.codecogs.com/")) {
                    message.editMessage(latexUrl).queue();
                    return;
                }
            }
            //            Message messageReply = latexUpdatedMessage.reply(latexUrl).setSuppressedNotifications(true).complete();
            //            messageReply.addReaction(Emoji.fromUnicode("🚮")).queue();
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        //LATEX
        if (!event.getReaction().isSelf()) {
            if (Objects.equals(event.getReaction().getEmoji(), Emoji.fromUnicode("🚮")) && event.retrieveMessage().complete().getAuthor().isBot() && event.retrieveMessage().complete().getContentRaw().contains("https://latex.codecogs.com/")) {
                if (event.retrieveUser().complete().getIdLong() == event.retrieveMessage().complete().getReferencedMessage().getAuthor().getIdLong()) {
                    event.retrieveMessage().complete().delete().queue();
                } else {
                    event.getReaction().removeReaction(event.retrieveUser().complete()).queue();
                }
            }
        }
    }

    private String getLatexUrl(String messageContent) {
        String latexString = messageContent.trim().substring(1, messageContent.trim().length() - 1); //remove $
        return "https://latex.codecogs.com/png.image?\\dpi{300}\\huge\\color{white}{\\phantom{space}" + latexString.replace(" ", "&space;").replace("\n", "&space;") + "\\phantom{space}}";
    }
}
package network.amnesia.anbd.games;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class HangmanGame {

    private final String word;
    private String hiddenWord;
    private String gameIllustration;
    private String status;
    private int colorCode;
    private int errorsCounter = 0;
    private String errorsLetters = "";
    private String letters = "";

    private InteractionHook gameMessage;
    private long threadChannel;

    public long getThreadChannel() {
        return threadChannel;
    }

    public static Set<HangmanGame> getGames() {
        return GAMES;
    }
    private static Set<HangmanGame> GAMES = new HashSet<>();

    public HangmanGame(String word) {
        this.word = word;
        hiddenWord = word.trim().replaceAll("[^ 0-9-]", "◉");
        updateGameIllustration();
        status = "Game in progress";
        colorCode = 1195176;

        GAMES.add(this);
    }

    public void setGameMessage(InteractionHook message) {
        this.gameMessage = message;
    }
    public void setThreadChannel(long threadChannel) {
        this.threadChannel = threadChannel;
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder gameEmbed = new EmbedBuilder();
        gameEmbed.setTitle("Game of Hangman");
        gameEmbed.setDescription(gameIllustration  + "\nGuess any letter in the word.\n" + hiddenWord.replace(" ", ":black_large_square:") + "\n");
        gameEmbed.addField("Errors :" , errorsLetters, true);
        gameEmbed.addField(status, "", false);
        gameEmbed.setColor(colorCode);
        return gameEmbed.build();
    }

    public void updateGameIllustration() {
        gameIllustration = switch(errorsCounter) {
            case 0 -> """
                    ```
                      _____________
                      |           |
                      |
                      |
                      |
                      |
                      |
                    /___\\
                    ```""";
            case 1 -> """
                    ```
                      _____________
                      |           |
                      |          \uD83D\uDE2E\s
                      |
                      |
                      |
                      |
                    /___\\
                    ```""";
            case 2 -> """
                    ```
                      _____________
                      |           |
                      |          \uD83D\uDE2E\s
                      |          \uD83D\uDC55\
                      
                      |
                      |
                      |
                    /___\\
                    ```""";
            case 3 -> """
                    ```
                      _____________
                      |           |
                      |          \uD83D\uDE2E\s
                      |        ,/\uD83D\uDC55\
                      
                      |
                      |
                      |
                    /___\\
                    ```""";
            case 4 -> """
                    ```
                      _____________
                      |           |
                      |          \uD83D\uDE2E\s
                      |        ,/\uD83D\uDC55\\,
                      |
                      |
                      |
                    /___\\
                    ```""";
            case 5 -> """
                    ```
                      _____________
                      |           |
                      |          \uD83D\uDE2E\s
                      |        ,/\uD83D\uDC55\\,
                      |          \uD83E\uDE73\s
                      |         -'
                      |
                    /___\\
                    ```""";
            case 6 -> """
                    ```
                      _____________
                      |           |
                      |          \uD83D\uDE2E\s
                      |        ,/\uD83D\uDC55\\,
                      |          \uD83E\uDE73\s
                      |         -''-
                      |
                    /___\\
                    ```""";
            case 7 -> """
                    ```
                      _____________
                      |           |
                      |          \uD83D\uDC80\s
                      |        ,/\uD83D\uDC55\\,
                      |          \uD83E\uDE73\s
                      |         -''-
                      |
                    /___\\
                    ```""";
            default -> throw new IllegalStateException("Unexpected value: " + errorsCounter);
        };
    }

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().trim().length() == 1) {
            String letter = event.getMessage().getContentRaw().toUpperCase();
            if(word.contains(letter)) {
                letters += letter;
                hiddenWord = word.trim().replaceAll("[^ 0-9-" + letters + "]", "◉");
                if(Objects.equals(word, hiddenWord)) { //WIN
                    status = "WIN !";
                    colorCode = 5763719;
                    GAMES.remove(this);
                }
            } else {
                errorsCounter++;
                errorsLetters += (errorsLetters.length() != 0 ? ", " : "") + letter;
                updateGameIllustration();
                if(errorsCounter == 7) { //DEFEAT
                    status = "DEFEAT !";
                    hiddenWord = word;
                    colorCode = 15548997;
                    GAMES.remove(this);
                }
            }
            gameMessage.editOriginalEmbeds(getEmbed()).complete();
            event.getMessage().replyEmbeds(getEmbed()).queue();
        }
    }
}
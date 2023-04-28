package network.amnesia.anbd.games;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import network.amnesia.anbd.Main;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class OxoGame {
    private static final Set<OxoGame> GAMES = new HashSet<>();
    private final long player1;
    private final long player2;
    private final String[] cells = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣"};
    private InteractionHook gameMessage;
    private long threadChannel;
    private long playerTurn;
    private String status;
    private int counter = 0;

    public OxoGame(long player1, long player2) {
        this.player1 = player1;
        this.player2 = player2;

        Random rand = new Random();
        int upperbound = 2;
        int int_random = rand.nextInt(upperbound);
        playerTurn = int_random == 0 ? player1 : player2;

        status = "It's " + Main.getJDA().retrieveUserById(playerTurn).complete().getName() + "'s turn.";

        GAMES.add(this);
    }

    public static Set<OxoGame> getGames() {
        return GAMES;
    }

    public long getThreadChannel() {
        return threadChannel;
    }

    public void setThreadChannel(long threadChannel) {
        this.threadChannel = threadChannel;
    }

    public void setGameMessage(InteractionHook message) {
        this.gameMessage = message;
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder gameEmbed = new EmbedBuilder();
        gameEmbed.setTitle("Game of OXO");
        gameEmbed.setDescription(cells[0] + cells[1] + cells[2] + "\n" + cells[3] + cells[4] + cells[5] + "\n" + cells[6] + cells[7] + cells[8]);
        gameEmbed.addField("Player 1 :o2:", Main.getJDA().retrieveUserById(player1).complete().getAsMention(), true);
        gameEmbed.addField("Player 2 :negative_squared_cross_mark:", Main.getJDA().retrieveUserById(player2).complete().getAsMention(), true);
        gameEmbed.addField(status, "", false);
        gameEmbed.setColor(0x006cff);
        return gameEmbed.build();
    }

    public int valueOfCell(String cell) {
        int value = 0;
        switch (cell) {
            case "1️⃣" -> value = 1;
            case "2️⃣" -> value = 2;
            case "3️⃣" -> value = 3;
            case "4️⃣" -> value = 4;
            case "5️⃣" -> value = 5;
            case "6️⃣" -> value = 6;
            case "7️⃣" -> value = 7;
            case "8️⃣" -> value = 8;
            case "9️⃣" -> value = 9;
        }
        return value;
    }

    private boolean isWin() {
        return (Objects.equals(cells[0], cells[1]) && Objects.equals(cells[1], cells[2])) ||
                (Objects.equals(cells[3], cells[4]) && Objects.equals(cells[4], cells[5])) ||
                (Objects.equals(cells[6], cells[7]) && Objects.equals(cells[7], cells[8])) ||
                (Objects.equals(cells[0], cells[3]) && Objects.equals(cells[3], cells[6])) ||
                (Objects.equals(cells[1], cells[4]) && Objects.equals(cells[4], cells[7])) ||
                (Objects.equals(cells[2], cells[5]) && Objects.equals(cells[5], cells[8])) ||
                (Objects.equals(cells[0], cells[4]) && Objects.equals(cells[4], cells[8])) ||
                (Objects.equals(cells[2], cells[4]) && Objects.equals(cells[4], cells[6]));
    }

    public boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
//        if(event.getAuthor().getIdLong() != player1 && event.getAuthor().getIdLong() != player2) {
//            event.getMessage().delete().queue();
//            return;
//        }
        if (event.getAuthor().getIdLong() == playerTurn) {
            String message = event.getMessage().getContentRaw();
            if (isStringInt(message)) {
                if (Integer.parseInt(message) <= 9 && Integer.parseInt(message) >= 1) {
                    int choice = Integer.parseInt(message);
                    String playerEmote = (playerTurn == player1 ? ":o2:" : ":negative_squared_cross_mark:");
                    boolean isFound = false;
                    for (int i = 0; i < cells.length; i++) {
                        if ((valueOfCell(cells[i]) != 0)) {
                            if (choice == valueOfCell(cells[i])) {
                                isFound = true;
                                counter++;
                                cells[i] = playerEmote;
                                if (isWin()) {
                                    status = "The winner is " + Main.getJDA().retrieveUserById(playerTurn).complete().getName();
                                    gameMessage.editOriginalEmbeds(getEmbed()).complete();
                                    event.getMessage().replyEmbeds(getEmbed()).complete();
                                    event.getChannel().sendMessage("The winner is " + Main.getJDA().retrieveUserById(playerTurn).complete().getAsMention() + "\nGame finished.").queue();
                                    GAMES.remove(this);
                                    return;
                                }
                                if (counter == 9) { //DRAW
                                    status = "DRAW !";
                                    gameMessage.editOriginalEmbeds(getEmbed()).complete();
                                    event.getMessage().replyEmbeds(getEmbed()).complete();
                                    event.getChannel().sendMessage("DRAW !" + "\nGame finished.").queue();
                                    GAMES.remove(this);
                                    return;
                                }
                                playerTurn = (playerTurn == player1 ? player2 : player1);
                                status = "It's " + Main.getJDA().retrieveUserById(playerTurn).complete().getName() + "'s turn.";
                                gameMessage.editOriginalEmbeds(getEmbed()).complete();
                                event.getMessage().replyEmbeds(getEmbed()).queue();
                                break;
                            }
                        }
                    }
                    if (!isFound) {
                        event.getMessage().replyFormat("Put on your glasses, this is not an available space.").queue();
                    }
                } else {
                    event.getMessage().replyFormat("Your choice must be between 1 and 9.").queue();
                }
            }
        }
    }
}

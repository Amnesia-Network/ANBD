package network.amnesia.anbd.polls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomPoll {
    private static final Set<CustomPoll> POLLS = new HashSet<>();
    private final ArrayList<String> options = new ArrayList<>();
    private final String[] nEmojis = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟"};
    private final ArrayList<Integer> counters = new ArrayList<>();
    private final ArrayList<ArrayList<Long>> votersIdLists = new ArrayList<>();
    private final Boolean allowChange;
    private int reactionIndex;
    private StringSelectMenu SelectMenu;
    private long pollMessageId;
    private String question = "No question";
    private Boolean anonymous = false;
    private Boolean multiple = false;

    public CustomPoll(String JSON_DATA, boolean allowChange) throws JsonProcessingException {
        this.allowChange = allowChange;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(JSON_DATA);
        JsonNode quest = json.get("question");
        JsonNode opt = json.get("options");
        JsonNode ano = json.get("anonymous");
        JsonNode multi = json.get("multiple");

        if (quest != null && !quest.textValue().trim().isEmpty()) {
            question = quest.textValue();
        }

        if (opt != null && opt.isArray()) {
            for (int i = 0; i < opt.size(); i++) {
                if (i > nEmojis.length - 1) { // MAXIMUM
                    break;
                }
                options.add(opt.get(i).textValue());
            }
        }
        if (ano != null && ano.isBoolean()) {
            anonymous = ano.booleanValue();
        }
        if (multi != null && multi.isBoolean()) {
            multiple = multi.booleanValue();
        }

        // Initialize counters and votersIdLists
        for (int i = 0; i < options.size(); i++) {
            counters.add(0);
            votersIdLists.add(new ArrayList<>());
        }

        if (anonymous) {
            StringSelectMenu.Builder SelectMenuBuilder = StringSelectMenu.create("select-menu").setMaxValues(multiple ? options.size() : 1);
            for (int i = 0; i < options.size(); i++) {
                SelectMenuBuilder.addOptions(SelectOption.of("Option " + (i + 1), String.valueOf(i))
                        .withDescription(options.get(i))
                        .withEmoji(Emoji.fromUnicode(nEmojis[i])
                        ));
            }
            SelectMenu = SelectMenuBuilder.build();
        }

        POLLS.add(this);
    }

    public static Set<CustomPoll> getPolls() {
        return POLLS;
    }

    public static String getPercentageBar(int percentage) {
        String bar = "⬛⬛⬛⬛⬛";
        if (percentage > 0 && percentage <= 20) {
            bar = "🟩⬛⬛⬛⬛";
        }
        if (percentage > 20 && percentage <= 40) {
            bar = "🟩🟩⬛⬛⬛";
        }
        if (percentage > 40 && percentage <= 60) {
            bar = "🟩🟩🟩⬛⬛";
        }
        if (percentage > 60 && percentage <= 80) {
            bar = "🟩🟩🟩🟩⬛";
        }
        if (percentage > 80 && percentage <= 100) {
            bar = "🟩🟩🟩🟩🟩";
        }
        return bar;
    }

    public long getPollMessageId() {
        return pollMessageId;
    }

    public void setPollMessageId(long messageId) {
        this.pollMessageId = messageId;
    }

    public boolean getAnonymousBoolean() {
        return anonymous;
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(question);
        for (int i = 0; i < options.size(); i++) {
            int counter = counters.get(i);
            embed.addField(nEmojis[i] + " " + options.get(i), getPercentageBar(getPercentage(counter, true)) + " " + getPercentage(counter, false) + "% (" + counter + ")", true);
        }
        embed.setColor(0x006cff);
        return embed.build();
    }

    public void addReactions(Message message) {
        if (anonymous) {
            message.editMessageEmbeds(getEmbed()).setActionRow(SelectMenu).complete();
        } else {
            for (int i = 0; i < options.size(); i++) {
                message.addReaction(Emoji.fromUnicode(nEmojis[i])).queue();
            }
        }
    }

    private int getPercentage(int counter, boolean pBar) {
        int total = pBar ? counters.stream().max(Integer::compare).get()
                : counters.stream().mapToInt(Integer::intValue).sum();
        return total == 0 ? 0 : Math.round(((float) counter / total) * 100);
    }

    public void onMessageReaction(GenericMessageReactionEvent event, boolean isAddReaction) {
        User user = event.retrieveUser().complete();
        Emoji reaction = event.getReaction().getEmoji();

        if (!isValidReaction(reaction)) {
            event.getReaction().removeReaction(user).queue();
            return;
        }

        long userIdLong = user.getIdLong();
        Message message = event.retrieveMessage().complete();
        if (isAddReaction) {
            if (!isVoter(userIdLong)) {
                votersIdLists.get(reactionIndex).add(userIdLong);
                counters.set(reactionIndex, counters.get(reactionIndex) + 1);
                message.editMessageEmbeds(getEmbed()).complete();
            } else {
                if (multiple) {
                    votersIdLists.get(reactionIndex).add(userIdLong);
                    counters.set(reactionIndex, counters.get(reactionIndex) + 1);
                    message.editMessageEmbeds(getEmbed()).complete();
                } else {
                    event.getReaction().removeReaction(user).queue();
                }
            }
        } else {
            if (isVoter(userIdLong) && !multiple) {
                if (allowChange) { // cause issue if user is spamming
                    int pos = -1;
                    for (ArrayList<Long> idList : votersIdLists) {
                        if (idList.contains(userIdLong)) {
                            pos = idList.indexOf(userIdLong);
                            break;
                        }
                    }
                    if (pos != -1 && Emoji.fromUnicode(nEmojis[pos]).equals(reaction)) {
                        votersIdLists.get(pos).remove(userIdLong);
                        counters.set(pos, counters.get(pos) - 1);
                        message.editMessageEmbeds(getEmbed()).complete();
                    }
                }
            } else {
//                if (!allowChange) { NO cause infinity add
//                    return;
//                }
                votersIdLists.get(reactionIndex).remove(userIdLong);
                counters.set(reactionIndex, counters.get(reactionIndex) - 1);
                message.editMessageEmbeds(getEmbed()).complete();
            }
        }
    }

    public void onSelectInteraction(StringSelectInteractionEvent event) {
        List<String> values = event.getInteraction().getValues();
        long userIdLong = event.getUser().getIdLong();
        if (!isVoter(userIdLong)) {
            for (String value : values) {
                int intValue = Integer.parseInt(value);
                votersIdLists.get(intValue).add(userIdLong);
                counters.set(intValue, counters.get(intValue) + 1);
            }
            event.editMessageEmbeds(getEmbed()).setActionRow(SelectMenu).complete();
        } else if (allowChange) {
            for (ArrayList<Long> idList : votersIdLists) {
                if (idList.contains(userIdLong)) {
                    counters.set(votersIdLists.indexOf(idList), counters.get(votersIdLists.indexOf(idList)) - 1);
                    idList.remove(userIdLong);
                }
            }
            for (String value : values) {
                int intValue = Integer.parseInt(value);
                votersIdLists.get(intValue).add(userIdLong);
                counters.set(intValue, counters.get(intValue) + 1);
            }
            event.editMessageEmbeds(getEmbed()).setActionRow(SelectMenu).complete();
        } else {
            event.reply("You already voted!").setEphemeral(true).queue();
        }
    }

    private boolean isVoter(long userIdLong) {
        for (ArrayList<Long> idList : votersIdLists) {
            if (idList.contains(userIdLong)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidReaction(Emoji reaction) {
        for (int i = 0; i < options.size(); i++) {
            if (Emoji.fromUnicode(nEmojis[i]).equals(reaction)) {
                reactionIndex = i;
                return true;
            }
        }
        return false;
    }
}
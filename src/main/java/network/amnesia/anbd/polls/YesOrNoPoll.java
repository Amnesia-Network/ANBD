package network.amnesia.anbd.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import network.amnesia.anbd.command.Button;

import java.util.ArrayList;

import static network.amnesia.anbd.polls.CustomPoll.getPercentageBar;

public class YesOrNoPoll {
    private final String question;
    private final ArrayList<Long> yesIdList = new ArrayList<Long>();
    private final ArrayList<Long> noIdList = new ArrayList<Long>();
    public Button yesButton = Button.success("Yes");
    public Button noButton = Button.danger("No");
    private int yesCounter = 0;
    private int noCounter = 0;
    private final Boolean allowChange;

    public YesOrNoPoll(String question, boolean allowChange) {
        this.question = question;
        this.allowChange = allowChange;

        yesButton.onClick(e -> {
            long userIdLong = e.getUser().getIdLong();
            if (allowChange) {
                if (noIdList.contains(userIdLong)) {
                    noIdList.remove(userIdLong);
                    noCounter--;
                    yesIdList.add(userIdLong);
                    yesCounter++;
                    e.editMessageEmbeds(getEmbed()).setActionRow(yesButton, noButton).complete();
                    //e.reply("Your 'No' vote has been successfully changed to a 'Yes' vote").setEphemeral(true).queue();
                    return;
                }
            }
            if (!yesIdList.contains(userIdLong) && !noIdList.contains(userIdLong)) {
                yesIdList.add(userIdLong);
                yesCounter++;
                e.editMessageEmbeds(getEmbed()).setActionRow(yesButton, noButton).complete();
                //e.reply("Your vote 'Yes' has been successfully added").setEphemeral(true).queue();
            } else {
                e.reply("You already voted!").setEphemeral(true).queue();
            }
        });
        noButton.onClick(e -> {
            long userIdLong = e.getUser().getIdLong();
            if (allowChange) {
                if (yesIdList.contains(userIdLong)) {
                    yesIdList.remove(userIdLong);
                    yesCounter--;
                    noIdList.add(userIdLong);
                    noCounter++;
                    e.editMessageEmbeds(getEmbed()).setActionRow(yesButton, noButton).complete();
                    //e.reply("Your 'Yes' vote has been successfully changed to a 'No' vote").setEphemeral(true).queue();
                    return;
                }
            }
            if (!yesIdList.contains(userIdLong) && !noIdList.contains(userIdLong)) {
                noIdList.add(userIdLong);
                noCounter++;
                e.editMessageEmbeds(getEmbed()).setActionRow(yesButton, noButton).complete();
                //e.reply("Your vote 'No' has been successfully added").setEphemeral(true).queue();
            } else {
                e.reply("You already voted!").setEphemeral(true).queue();
            }
        });
    }

    public MessageEmbed getEmbed() {
        int yesPercentage = getPercentage(yesCounter);
        int noPercentage = getPercentage(noCounter);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(question);
        embed.addField("Yes", getPercentageBar(yesPercentage) + " " + yesPercentage + "% (" + yesCounter + ")", false);
        embed.addField("No", getPercentageBar(noPercentage) + " " + noPercentage + "% (" + noCounter + ")", false);
        embed.setColor(0x006cff);
        return embed.build();
    }

    private int getPercentage(int counter) {
        return counter == 0 ? 0 : Math.round(((float) counter / (yesCounter + noCounter)) * 100);
    }
}
package network.amnesia.anbd.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.gameinfo.GameInfoList;
import network.amnesia.anbd.gameinfo.GameLookupManager;
import network.amnesia.anbd.gameinfo.SortingCriteria;

import java.io.IOException;
import java.util.stream.Collectors;

@ICommand(name = "gamelookup", category = CommandCategory.INFO, description = "Get a list of widgets describing games matching the search")
public class GameLookupCommand extends Command {


    public Outcome invoke(SlashCommandInteractionEvent event, String title) {
        return invoke(event, title, SortingCriteria.Reviews.toString());
    }

    public Outcome invoke(SlashCommandInteractionEvent event, String title, String order) {
        event.deferReply();

        SortingCriteria sortingCriteria = null;
        try {
             sortingCriteria = SortingCriteria.valueOf(order);
        } catch (IllegalArgumentException e) {
            event.replyFormat("%s Incorrect order option", Constants.SKULL_EMOTE).setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        GameInfoList gameInfoList = null;
        try {
            gameInfoList = new GameInfoList(title, sortingCriteria);
        } catch (JsonProcessingException e) {
            event.replyFormat("%s error while processing data", Constants.SKULL_EMOTE).setEphemeral(true).queue();
            return Outcome.ERROR;
        } catch (IOException e) {
            event.replyFormat("%s error while fetching data", Constants.SKULL_EMOTE).setEphemeral(true).queue();
            return Outcome.ERROR;
        }

        if (!gameInfoList.hasContent()) {
            event.replyFormat("%s no match found", Constants.X_EMOTE).setEphemeral(true).queue();
            return Outcome.SUCCESS;
        }

        GameLookupManager gameLookupManager = GameLookupManager.forGuild(event.getGuild());
        gameLookupManager.replyMessage(event, gameInfoList);
        return Outcome.SUCCESS;
    }


    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData()
                .addOption(OptionType.STRING, "title", "The title of the game you want to know about", true)
                .addOption(OptionType.STRING, "order", "DealRating, Title Savings, Price, Metacritic, Reviews,  Release, Store or Recent", false);
    }

}
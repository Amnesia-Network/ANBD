package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.games.OxoGame;


@ICommand(name = "oxo", category = CommandCategory.FUN, description = "Play a game of OXO with another player")
public class OxoCommand extends Command {

    public Outcome invoke(SlashCommandInteractionEvent event, User user) {

        if (event.getChannel().getType().isThread()) {
            event.reply("You can't play in a ThreadChannel.").setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        long player1Id = event.getUser().getIdLong();
        long player2Id = user.getIdLong();

        if (player1Id == player2Id) {
            event.reply("You can't play with yourself.").setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        OxoGame game = new OxoGame(player1Id, player2Id);
        InteractionHook message = event.replyEmbeds(game.getEmbed()).complete();
        game.setGameMessage(message);
        game.setThreadChannel(message.retrieveOriginal().complete().createThreadChannel("Oxo Game").complete().getIdLong());
        event.getGuild().getThreadChannelById(game.getThreadChannel()).addThreadMemberById(player1Id).queue();
        event.getGuild().getThreadChannelById(game.getThreadChannel()).addThreadMemberById(player2Id).queue();

        return Outcome.SUCCESS;
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.USER, "user", "Your game's partner", true);
    }
}
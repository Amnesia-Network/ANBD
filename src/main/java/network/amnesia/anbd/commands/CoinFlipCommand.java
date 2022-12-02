package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.Constants;

import java.util.Random;

@ICommand(name = "coinflip", category = CommandCategory.FUN, description = "Flip a coin")
public class CoinFlipCommand extends Command {
    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        Random rand = new Random();
        int upperbound = 2;
        int int_random = rand.nextInt(upperbound);

        event.replyFormat(int_random == 0 ? Constants.ANCOIN_TAIL : Constants.ANCOIN_HEAD).queue();
        return Outcome.SUCCESS;
    }
}
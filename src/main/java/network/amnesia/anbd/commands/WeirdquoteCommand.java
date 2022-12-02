package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

@ICommand(name = "weirdquote", category = CommandCategory.FUN, description = "Generate a weird quote")
public class WeirdquoteCommand extends Command {

    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        event.replyFormat("This feature will be available in the next update.").setEphemeral(true).queue(); //example
        return Outcome.SUCCESS;
    }
}

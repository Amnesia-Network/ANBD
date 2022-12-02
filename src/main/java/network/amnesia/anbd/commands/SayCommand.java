package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

@ICommand(name = "say", category = CommandCategory.MODERATION, description = "Ask the bot to send a message", restricted = true)
public class SayCommand extends Command {

    public Outcome invoke(SlashCommandInteractionEvent event, String message) {
            event.getChannel().sendMessage(message).queue();
            event.replyFormat("Message sent.").setEphemeral(true).queue();
            return Outcome.SUCCESS;
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "message", "The message you want the bot to send", true);
    }
}

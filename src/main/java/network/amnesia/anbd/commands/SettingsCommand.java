package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

@ICommand(name = "settings", category = CommandCategory.GUILD, defaultPermissions = {Permission.ADMINISTRATOR}, description = "Manage guild settings")
public class SettingsCommand extends Command {

    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        event.reply("Please specify subcommand").setEphemeral(true).queue();
        return Outcome.INCORRECT_USAGE;
    }
}

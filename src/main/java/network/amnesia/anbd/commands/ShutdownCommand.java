package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.Main;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

@ICommand(name = "shutdown", category = CommandCategory.SYSTEM, description = "Shutdown the bot", guildOnly = false, restricted = true)
public class ShutdownCommand extends Command {
    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        return invoke(event, false);
    }

    public Outcome invoke(SlashCommandInteractionEvent event, boolean force) {
        event.replyFormat("Shutting down (%d)", force ? -1 : 0).setEphemeral(true).complete();
        Main.exit(force ? -1 : 0);
        return Outcome.SUCCESS;
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.BOOLEAN, "force", "force shutdown", false);
    }
}

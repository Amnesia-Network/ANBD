package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.Constants;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

import java.util.concurrent.TimeUnit;

@ICommand(name = "purge", category = CommandCategory.MODERATION, description = "Delete messages in bulk",
        defaultPermissions = Permission.MESSAGE_MANAGE)
public class PurgeCommand extends Command {

    public Outcome invoke(SlashCommandInteractionEvent event, int amount) {
        event.deferReply().queue();
        event.getChannel().getIterableHistory()
                .takeAsync(amount)
                .thenAccept(event.getChannel()::purgeMessages);
        event.getHook().editOriginalFormat("%s Deleted %d messages", Constants.CHECK_EMOTE, amount)
                .delay(4, TimeUnit.SECONDS)
                .flatMap(Message::delete)
                .queue();
        return Outcome.SUCCESS;
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.INTEGER, "amount", "number of messages to delete", true);
    }
}

package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import network.amnesia.anbd.command.ICommand;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.Command;

@ICommand(name = "ping", category = CommandCategory.SYSTEM, description = "Get the ping of the bot", guildOnly = false)
public class PingCommand extends Command {
    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.deferReply()
                .flatMap(v -> event.getHook().editOriginalFormat("Ping: %d ms", System.currentTimeMillis() - time))
                .queue();
        return Outcome.SUCCESS;
    }
}

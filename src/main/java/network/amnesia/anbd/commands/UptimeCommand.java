package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import network.amnesia.anbd.Main;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

import java.time.Duration;

@ICommand(name = "uptime", category = CommandCategory.SYSTEM, description = "Get the uptime of the bot", guildOnly = false)
public class UptimeCommand extends Command {
    @Override
    public Outcome invoke(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        Duration uptime = Duration.ofMillis(time - Main.APP_START_TIME);
        String uptimeStr = uptime.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();

        event.replyFormat("Uptime: %s", uptimeStr).setEphemeral(true).queue();
        return Outcome.SUCCESS;
    }
}

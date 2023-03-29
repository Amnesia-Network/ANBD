package network.amnesia.anbd.command;

import com.google.common.collect.ImmutableSet;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.Main;
import network.amnesia.anbd.RuntimeStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandManager {


    private static final Logger LOG = LogManager.getLogger();
    private static Set<Command> COMMANDS;

    public void register(Set<Command> commands) {
        if (COMMANDS != null) throw new IllegalStateException("Commands have already been registered");
        COMMANDS = ImmutableSet.copyOf(commands);

        Set<SlashCommandData> commandDataSet = COMMANDS.stream()
                .filter(command -> !command.isSubCommand())
                .map(Command::getCommandData)
                .collect(Collectors.toUnmodifiableSet());

        COMMANDS.stream().filter(Command::isSubCommand).forEach(subcommand -> {
            commandDataSet.stream()
                    .filter(command -> command.getName().equals(subcommand.getCommandName()))
                    .findFirst()
                    .get()
                    .addSubcommands(subcommand.getSubcommandData());
        });

        List<net.dv8tion.jda.api.interactions.commands.Command> jdaCommands =
                Main.getJDA().updateCommands().addCommands(commandDataSet).complete();

        LOG.info("Registered {} commands", jdaCommands.size());
    }

    public void handleEvent(GenericCommandInteractionEvent event) {
        if (event instanceof SlashCommandInteractionEvent) {
            handleSlashCommandInteractionEvent((SlashCommandInteractionEvent) event);
        }
    }

    private void handleSlashCommandInteractionEvent(SlashCommandInteractionEvent event) {
        COMMANDS.forEach(command -> {
            if (!command.getICommand().name().equals(event.getFullCommandName())) return;
            RuntimeStatistics.recordOutcome(command._invoke(event));
        });
    }
}

package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ICommand(name = "roll", category = CommandCategory.FUN, description = "Roll [amount] dice(s) of [sides]")
public class RollCommand extends Command {

    public Outcome invoke(SlashCommandInteractionEvent event, int amount, int sides) {

        if(amount <= 0) {
            event.replyFormat("Please set positive amount of dices to roll [>0]").setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }
        if(sides <= 0) {
            event.replyFormat("Please set a positive number of dice sides [>0]").setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        String rolls = IntStream.range(1, amount).map(ignored -> ThreadLocalRandom.current().nextInt(sides) + 1).mapToObj(i -> numberToEmote(i, sides)).collect(Collectors.joining(", "));

        event.replyFormat("rolled %d %s of %d: %s.", amount, amount > 1 ? "dice" : "die", sides, rolls).queue();
        return Outcome.SUCCESS;
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.INTEGER, "amount", "Amount of dices to roll [>0]", true)
                                     .addOption(OptionType.INTEGER, "sides", "Number of dice sides to set [>0]", true);
    }

    private String numberToEmote(int number, int sides) {
        if (sides > 10) return String.valueOf(number);
        return switch (number) {
            case 1 -> ":one:";
            case 2 -> ":two:";
            case 3 -> ":three:";
            case 4 -> ":four:";
            case 5 -> ":five:";
            case 6 -> ":six:";
            case 7 -> ":seven:";
            case 8 -> ":eight:";
            case 9 -> ":nine:";
            case 10 -> ":ten:";
            default -> String.valueOf(number);
        };
    }
}
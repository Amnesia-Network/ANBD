package network.amnesia.anbd.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.command.CommandCategory;
import network.amnesia.anbd.command.ICommand;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ICommand(name = "roll", category = CommandCategory.FUN, description = "Roll [amount] dice of [sides]")
public class RollCommand extends Command {

    private final String[] nEmojis = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟"};

    public Outcome invoke(SlashCommandInteractionEvent event, int amount, int sides) {

        if (amount <= 0) {
            event.reply("Please set positive amount of dice to roll [>0]").setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }
        if (sides <= 0) {
            event.reply("Please set a positive number of die sides [>0]").setEphemeral(true).queue();
            return Outcome.INCORRECT_USAGE;
        }

        String rolls = IntStream.range(0, amount).map(ignored -> ThreadLocalRandom.current().nextInt(sides) + 1).mapToObj(i -> numberToEmoji(i, sides)).collect(Collectors.joining(", "));

        event.replyFormat("rolled %d %s of %d: %s.", amount, amount > 1 ? "dice" : "die", sides, rolls).queue();
        return Outcome.SUCCESS;
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.INTEGER, "amount", "Amount of dice to roll [>0]", true)
                .addOption(OptionType.INTEGER, "sides", "Number of die sides to set [>0]", true);
    }

    private String numberToEmoji(int number, int sides) {
        if (sides > 10) return String.valueOf(number);
        return nEmojis[number - 1];
    }
}
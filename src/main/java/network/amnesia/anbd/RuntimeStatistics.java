package network.amnesia.anbd;

import network.amnesia.anbd.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class RuntimeStatistics {
    private static final Logger LOG = LogManager.getLogger();

    private static final Map<Command.Outcome, Integer> commandOutcomes = new HashMap<>();

    public static void recordOutcome(Command.Outcome outcome) {
        commandOutcomes.merge(outcome, 1, Integer::sum);
    }

    public static Map<Command.Outcome, Integer> getCommandOutcomes() {
        return Collections.unmodifiableMap(commandOutcomes);
    }

    public static void print() {

        List<String[]> data = new ArrayList<>();

        int total = RuntimeStatistics.getCommandOutcomes().values().stream().reduce(Integer::sum).orElse(0);
        RuntimeStatistics.getCommandOutcomes().forEach((outcome, integer) -> {
            data.add(Utils.arrayOf(outcome.toString(), integer.toString(), String.valueOf((integer / total) * 100)));
        });

        Arrays.asList(Utils.formatTableForLogging("Command Outcomes",
                                Utils.arrayOf("Outcome", "Amount", "Percentage"),
                                data.toArray(new String[data.size()][]))
                        .split("\n")).forEach(LOG::info);
    }
}

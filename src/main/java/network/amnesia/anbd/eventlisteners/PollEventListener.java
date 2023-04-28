package network.amnesia.anbd.eventlisteners;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import network.amnesia.anbd.polls.CustomPoll;
import org.jetbrains.annotations.NotNull;

public class PollEventListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (!event.retrieveUser().complete().isBot()) {
            CustomPoll.getPolls().stream().filter(p -> p.getPollMessageId() == event.getMessageIdLong() && !p.getAnonymousBoolean()).findFirst().ifPresent(p -> p.onMessageReaction(event, true));
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if (!event.retrieveUser().complete().isBot()) {
            CustomPoll.getPolls().stream().filter(p -> p.getPollMessageId() == event.getMessageIdLong() && !p.getAnonymousBoolean()).findFirst().ifPresent(p -> p.onMessageReaction(event, false));
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        CustomPoll.getPolls().stream().filter(p -> p.getPollMessageId() == event.getMessageIdLong() && p.getAnonymousBoolean()).findFirst().ifPresent(p -> p.onSelectInteraction(event));
    }
}

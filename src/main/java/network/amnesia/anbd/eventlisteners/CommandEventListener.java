package network.amnesia.anbd.eventlisteners;

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import network.amnesia.anbd.Main;
import org.jetbrains.annotations.NotNull;

public class CommandEventListener extends ListenerAdapter {
    @Override
    public void onGenericCommandInteraction(@NotNull GenericCommandInteractionEvent event) {
        Main.getCommandManager().handleEvent(event);
    }
}

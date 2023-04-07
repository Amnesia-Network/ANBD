package network.amnesia.anbd.command;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

@FunctionalInterface
public interface ButtonCallback {
    void run(ButtonInteractionEvent event);
}

package network.amnesia.anbd.eventlisteners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import network.amnesia.anbd.Main;
import org.jetbrains.annotations.NotNull;

public class ButtonEventListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Main.getButtonManager().handleEvent(event);
    }
}

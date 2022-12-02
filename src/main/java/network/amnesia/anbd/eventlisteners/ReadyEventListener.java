package network.amnesia.anbd.eventlisteners;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import network.amnesia.anbd.Main;
import org.jetbrains.annotations.NotNull;

public class ReadyEventListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Main.JDAReady();
    }
}

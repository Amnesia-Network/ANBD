package network.amnesia.anbd.eventlisteners;

import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ExceptionEventListener extends ListenerAdapter {
    private static final Logger LOG = LogManager.getLogger();

    @Override
    public void onException(@NotNull ExceptionEvent event) {
        LOG.warn("JDA Error");
    }
}

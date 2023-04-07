package network.amnesia.anbd.command;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.HashMap;

public class ButtonManager {

    private static final BiMap<String, ButtonCallback> CALLBACKS = HashBiMap.create();

    public void registerCallback(String id, ButtonCallback callback) {
        CALLBACKS.put(id, callback);
    }

    public void removeCallback(String id) {
        CALLBACKS.remove(id);
    }

    public void removeCallback(ButtonCallback buttonCallback) {
        CALLBACKS.remove(CALLBACKS.inverse().get(buttonCallback));
    }

    public ButtonCallback getCallback(String id) {
        return CALLBACKS.get(id);
    }

    public void handleEvent(ButtonInteractionEvent event) {
        ButtonCallback callback = getCallback(event.getComponentId());
        if (callback != null) callback.run(event);
    }

    static {
        // Permanent callbacks
    }
}

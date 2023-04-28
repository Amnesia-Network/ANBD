package network.amnesia.anbd.factories;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import network.amnesia.anbd.Factory;
import network.amnesia.anbd.Main;
import network.amnesia.anbd.exceptions.ReflectiveOperationRuntimeException;

public class EventListenerFactory extends Factory<ListenerAdapter> {

    public boolean register() {
        try {
            Main.getJDA().addEventListener(load("network.amnesia.anbd.eventlisteners").toArray());
        } catch (ReflectiveOperationRuntimeException e) {
            return false;
        }
        return true;
    }
}

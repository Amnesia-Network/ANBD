package network.amnesia.anbd.factories;

import network.amnesia.anbd.Factory;
import network.amnesia.anbd.Main;
import network.amnesia.anbd.command.Command;
import network.amnesia.anbd.exceptions.ReflectiveOperationRuntimeException;

public class CommandFactory extends Factory<Command> {

    public boolean register() {
        try {
            Main.getCommandManager().register(load("network.amnesia.anbd.commands"));
        } catch (ReflectiveOperationRuntimeException e) {
            return false;
        }
        return true;
    }
}

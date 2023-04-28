package network.amnesia.anbd.factories;

import network.amnesia.anbd.Factory;
import network.amnesia.anbd.exceptions.ReflectiveOperationRuntimeException;

public class FactoryFactory extends Factory<Factory> {
    private static boolean registering;

    public boolean register() {
        if (registering) return true;
        registering = true;

        try {
            return load("network.amnesia.anbd.factories").stream().allMatch(Factory::register);
        } catch (ReflectiveOperationRuntimeException e) {
            return false;
        }
    }
}

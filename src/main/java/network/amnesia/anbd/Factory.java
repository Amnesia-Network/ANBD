package network.amnesia.anbd;

import network.amnesia.anbd.exceptions.ReflectiveOperationRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.ParameterizedType;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Factory<T> {
    private static final Logger LOG = LogManager.getLogger();

    private boolean loaded;

    public abstract boolean register();

    @SuppressWarnings("unchecked")
    protected final Set<T> load(String package_) throws ReflectiveOperationRuntimeException {
        if (loaded) throw new IllegalStateException(String.format("%s already loaded", this.getClass().getName()));

        Reflections reflections = new Reflections(package_);

        Set<Class<?>> classes = reflections.get(Scanners.SubTypes.of(getGenericTypeClass()).asClass());

        Set<T> instances = classes.stream().map(clazz -> {
            try {
                LOG.info("Loading {}", clazz.getSimpleName());
                return createInstance((Class<T>) clazz);
            } catch (ReflectiveOperationException e) {
                throw new ReflectiveOperationRuntimeException(e);
            }
        }).collect(Collectors.toUnmodifiableSet());

        loaded = true;

        LOG.info("Loaded {} {}", instances.size(), getGenericTypeClass().getSimpleName());

        return instances;
    }

    @SuppressWarnings("unchecked")
    private Class<T> getGenericTypeClass() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    protected T createInstance(Class<T> clazz) throws ReflectiveOperationException {
        return clazz.getConstructor().newInstance();
    };
}

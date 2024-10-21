package de.tomalbrc.filament.api.registry;

import de.tomalbrc.filament.Filament;
import de.tomalbrc.filament.api.behaviour.Behaviour;
import de.tomalbrc.filament.api.behaviour.BehaviourType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class BehaviourRegistry {
    private static final Map<ResourceLocation, BehaviourType<? extends Behaviour<?>, ?>> behaviourMap = new Object2ObjectOpenHashMap<>();

    public static <T extends Behaviour<E>,E> BehaviourType<T, E> registerBehaviour(ResourceLocation resourceLocation, Class<T> type) {
        Class<E> configType = BehaviourRegistry.inferConfigType(type);
        BehaviourType<T, E> behaviourType = new BehaviourType(resourceLocation, type, configType);
        behaviourMap.put(resourceLocation, behaviourType);
        return behaviourType;
    }

    @SuppressWarnings("unchecked")
    private static <E> Class<E> inferConfigType(Class<? extends Behaviour<?>> behaviour) {
        Type[] genericInterfaces = behaviour.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType parameterizedType) {
                return (Class<E>)parameterizedType.getActualTypeArguments()[0];
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Behaviour<E>, E> BehaviourType<T, E> getType(ResourceLocation key) {
        BehaviourType<?, ?> info = behaviourMap.get(key);
        if (info == null) {
            Filament.LOGGER.error("Could not find behaviour " + key);
            return null;
        }
        return (BehaviourType<T, E>) behaviourMap.get(info.id());
    }
}

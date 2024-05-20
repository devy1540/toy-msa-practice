package com.practice.devy.utils;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class TypeCaster {

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj, Class<T> clazz) {
        if(Objects.isNull(obj)) throw new ClassCastException("Cannot cast null object to %s".formatted(clazz.getName()));

        if(clazz.isInstance(obj)) return (T) obj;
        else throw new ClassCastException(String.format("Cannot cast object to %s", clazz.getName()));
    }


    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj, TypeReference<T> typeRef) {
        Type type = typeRef.getType();

        if (type instanceof ParameterizedType paramType) {
            Class<?> rawType = (Class<?>) paramType.getRawType();

            if (obj == null) throw new ClassCastException("Cannot cast null object to %s".formatted(type));
            if (rawType.isInstance(obj)) {
                if (obj instanceof Collection<?>) {
                    checkCollectionElements((Collection<?>) obj, paramType.getActualTypeArguments()[0]);
                } else if (obj instanceof Map<?, ?>) {
                    checkMapEntries((Map<?, ?>) obj, paramType.getActualTypeArguments()[0], paramType.getActualTypeArguments()[1]);
                }
                return (T) obj;
            } else throw new ClassCastException("Cannot cast object to %s".formatted(type));
        } else {
            if (((Class<?>) type).isInstance(obj)) return (T) obj;
            else throw new ClassCastException("Cannot cast object to %s".formatted(type));
        }
    }

    private static void checkCollectionElements(Collection<?> c, Type t) {
        for (Object e : c) {
            if (!((Class<?>) t).isInstance(e)) throw new ClassCastException("Collection contains elements not of type %s".formatted(t));
        }
    }

    private static void checkMapEntries(Map<?, ?> m, Type k, Type v) {
        for (Map.Entry<?, ?> entry : m.entrySet()) {
            if (!((Class<?>) k).isInstance(entry.getKey()) || !((Class<?>) v).isInstance(entry.getValue())) {
                throw new ClassCastException("Map contains entries not of type " + k + " and " + v);
            }
        }
    }
}

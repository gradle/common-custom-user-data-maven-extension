package com.gradle.ccud.adapters;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.gradle.ccud.adapters.ReflectionUtils.invokeMethod;

public final class Property<T> {

    private final Consumer<T> setter;
    private final Supplier<T> getter;

    private Property(Consumer<T> setter, Supplier<T> getter) {
        this.setter = setter;
        this.getter = getter;
    }

    public static <T> Property<T> create(Consumer<T> setter, Supplier<T> getter) {
        return new Property<>(setter, getter);
    }

    public static <T> Property<T> optional(Object obj, String setterName, String getterName) {
        return new Property<>(
            value -> setIfSupported(obj, setterName, value),
            () -> getIfSupported(obj, getterName)
        );
    }

    public void set(T value) {
        setter.accept(value);
    }

    public T get() {
        return getter.get();
    }

    private static <T> void setIfSupported(Object obj, String method, T value) {
        invokeMethod(obj, method, value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getIfSupported(Object obj, String method) {
        return (T) invokeMethod(obj, method).orElse(null);
    }

}

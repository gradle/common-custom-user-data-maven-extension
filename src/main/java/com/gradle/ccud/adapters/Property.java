package com.gradle.ccud.adapters;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Property<T> {

    private final Consumer<T> setter;
    private final Supplier<T> getter;

    public Property(Consumer<T> setter, Supplier<T> getter) {
        this.setter = setter;
        this.getter = getter;
    }

    public void set(T value) {
        setter.accept(value);
    }

    public T get() {
        return getter.get();
    }
}
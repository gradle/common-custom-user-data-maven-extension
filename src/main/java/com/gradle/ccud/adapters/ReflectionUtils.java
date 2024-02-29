package com.gradle.ccud.adapters;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

import static com.gradle.CommonCustomUserDataDevelocityLogger.LOGGER;

public class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static Optional<Object> invokeMethod(Object obj, String method, Object... args) {
        try {
            Class<?>[] argClasses = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
            return Optional.ofNullable(obj.getClass().getMethod(method, argClasses).invoke(obj, args));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            warnAboutUnsupportedMethod(method);
            return Optional.empty();
        }
    }

    public static void withMethodSupported(Object obj, String method, Runnable action) {
        if (isMethodSupported(obj, method)) {
            action.run();
        } else {
            warnAboutUnsupportedMethod(method);
        }
    }

    private static boolean isMethodSupported(Object obj, String method) {
        return Arrays.stream(obj.getClass().getMethods())
            .anyMatch(it -> it.getName().equals(method));
    }

    private static void warnAboutUnsupportedMethod(String method) {
        LOGGER.warn("The '{}' method is not supported by this version of the Develocity extension", method);
    }
}

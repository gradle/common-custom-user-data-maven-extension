package com.gradle.ccud.adapters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.gradle.ccud.adapters.ReflectionUtils.invokeMethod;
import static com.gradle.ccud.adapters.ReflectionUtils.withMethodSupported;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReflectionUtilsTest {

    private static class Subject {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @Test
    @DisplayName("can invoke supported void methods")
    void testInvokeVoidMethodsWithArgs() {
        // given
        Subject s = new Subject();

        // when
        invokeMethod(s, "setValue", 11);

        // then
        assertEquals(11, s.value);
    }

    @Test
    @DisplayName("can invoke methods returning values")
    void testInvokeGettersWithNoArgs() {
        // given
        Subject s = new Subject();
        s.setValue(22);

        // expect
        assertEquals(22, invokeMethod(s, "getValue"));
    }

    @Test
    @DisplayName("does not fail if the requested method is unsupported")
    void testInvokeMissingMethods() {
        // given
        Subject s = new Subject();

        // when
        assertNull(invokeMethod(s, "unsupported"));
        assertNull(invokeMethod(s, "unsupported", 42));
    }

    @Test
    @DisplayName("can invoke requested action if method used is supported")
    void testInvokeActionIfMethodSupported() {
        // given
        Subject s = new Subject();

        // when
        withMethodSupported(s, "setValue", () -> s.setValue(33));

        // then
        assertEquals(33, s.value);
    }

    @Test
    @DisplayName("does not perform the action if method requested is unsupported")
    void testInvokeActionIfMethodUnsupported() {
        // given
        Subject s = new Subject();
        s.setValue(44);

        // when
        withMethodSupported(s, "unsupported", () -> s.setValue(55));

        // then
        assertEquals(44, s.value);
    }

}